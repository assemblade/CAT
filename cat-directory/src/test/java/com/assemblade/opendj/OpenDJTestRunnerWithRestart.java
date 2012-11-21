/*
 * Copyright 2012 Mike Adamson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.assemblade.opendj;

import org.apache.commons.io.FileUtils;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.File;
import java.lang.reflect.Method;

public class OpenDJTestRunnerWithRestart extends BlockJUnit4ClassRunner {
    private static final String DATA_STORE_LOCATION = System.getProperty("java.io.tmpdir") + "/opendj";

    OpenDJDirectoryService directoryService;

    public OpenDJTestRunnerWithRestart(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        try {
            Method setService = getTestClass().getJavaClass().getMethod( "setDirectoryService", DirectoryService.class);
            setService.invoke(getTestClass().getJavaClass(), directoryService);
            super.runChild(method, notifier);
            OpenDJUtils.refreshRootEntry();
            directoryService.restart();
        } catch (Exception e) {
            notifier.fireTestFailure(new Failure(getDescription(), e));
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            FileUtils.deleteDirectory(new File(DATA_STORE_LOCATION));
            directoryService = new OpenDJDirectoryService(DATA_STORE_LOCATION);
            super.run(notifier);
            directoryService.stop();
        } catch (Exception e) {
            notifier.fireTestFailure(new Failure(getDescription(), e));
        }
    }


}
