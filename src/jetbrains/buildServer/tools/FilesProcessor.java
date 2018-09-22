package jetbrains.buildServer.tools;
/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import jetbrains.buildServer.tools.fs.FSScanFileBase;
import jetbrains.buildServer.tools.step.CheckFileScanStep;
import jetbrains.buildServer.tools.step.DirectoryScanStep;
import jetbrains.buildServer.tools.step.ScanZipStep;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 11.11.11 16:07
 */
public class FilesProcessor implements Continuation {
  private static final AtomicInteger ourThreadNameCounter = new AtomicInteger();

  public static void processFiles(@NotNull final File scanHome,
                                  @NotNull final CheckSettings settings,
                                  @NotNull final ErrorReporting reporting) {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(), 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>()) {
      {
        setThreadFactory(r -> {
          final Thread thread = new Thread(r);
          thread.setName("Files Processor " + ourThreadNameCounter.getAndIncrement());
          return thread;
        });
      }
    };
    final FilesProcessor processor = new FilesProcessor(settings, reporting, executor);
    try {
      processor.processRoot(scanHome);
    } catch (Throwable e) {
      if (e instanceof RuntimeException) throw ((RuntimeException) e);
      throw new RuntimeException(e);
    } finally {
      executor.shutdown();
      try {
        executor.awaitTermination(10, TimeUnit.SECONDS);
      } catch (InterruptedException ignored) {
      }
      executor.shutdownNow();
    }
  }


  private final ThreadPoolExecutor myExecutor;
  private final ConcurrentLinkedDeque<Future> myFutures = new ConcurrentLinkedDeque<>();

  private final AtomicInteger myProcessed = new AtomicInteger();
  @NotNull
  private final CheckSettings mySettings;
  @NotNull
  private final ErrorReporting myReporting;
  private final ScanStep[] mySteps;

  private FilesProcessor(@NotNull final CheckSettings settings,
                         @NotNull final ErrorReporting reporting,
                         @NotNull final ThreadPoolExecutor executor) {
    mySettings = settings;
    myReporting = reporting;
    myExecutor = executor;
    mySteps = new ScanStep[]{
            new CheckFileScanStep(mySettings, myReporting),
            new ScanZipStep(),
            new DirectoryScanStep(),
    };
  }

  private void processRoot(@NotNull final File scanHome) throws InterruptedException {
    final FSScanFileBase base = new FSScanFileBase(scanHome) {
      @NotNull
      @Override
      public String getName() {
        return "";
      }
    };
    this.postTask(base);

    // Wait till all files processed
    while (!myFutures.isEmpty()) {
      myFutures.removeIf(Future::isDone);
      Future lastNonDone = myFutures.peekLast();
      if (lastNonDone != null) {
        try {
          lastNonDone.get();
        } catch (InterruptedException e) {
          throw e;
        } catch (Throwable e) {
          // Since we use #get as waiting, let's wait other way
          Thread.sleep(100);
        }
      }
    }
  }

  @Override
  public void postTask(@NotNull final ScanFile file) {
    if (file.isPhysical()) {
      myFutures.add(myExecutor.submit(() -> process(file)));
    } else {
      process(file);
    }
  }

  private void process(@NotNull final ScanFile file) {
    if (mySettings.isDebugMode()) {
      System.out.println("scanning: " + file.getName());
    } else {
      final int cnt = myProcessed.incrementAndGet();
      if (cnt % 100 == 0) {
        System.out.print(".");
      }
      if (cnt % 4000 == 0) {
        System.out.println();
      }
    }

    if (mySettings.isPathExcluded(file)) {
      System.out.print("S");
      return;
    }

    for (ScanStep step : mySteps) {
      try {
        step.process(file, this);
      } catch (Throwable e) {
        myReporting.postError(file, e.toString());
      }
    }
  }
}
