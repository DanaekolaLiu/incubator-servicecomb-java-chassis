/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicecomb.it.deploy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubProcessLogger implements Closeable {
  private static final Logger LOGGER = LoggerFactory.getLogger(SubProcessLogger.class);

  private final String displayName;

  private BufferedReader reader;

  private Thread thread;

  public SubProcessLogger(String displayName, InputStream inputStream) {
    this.displayName = displayName;

    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
    this.reader = new BufferedReader(new InputStreamReader(bufferedInputStream));

    thread = new Thread(this::run, "SubProcessLogger-" + displayName);
    thread.start();
  }

  private void run() {
    try {
      doRun();
    } catch (IOException e) {
      LOGGER.error("Failed to read log.", e);
    }
  }

  private void doRun() throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      System.out.print(String.format("[%s] ", displayName));
      System.out.println(line);
    }
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }
}
