/*
 * Copyright 2015 to CloudModelExplorer authors
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

package org.xid.explorer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by j5r on 24/12/2014.
 */
public class PathResourceResolver implements ResourceResolver {

    private final Path root;

    public PathResourceResolver(Path root) {
        this.root = root;
    }

    public Path getRoot() {
        return root;
    }

    @Override
    public InputStream readEntry(String path) throws IOException {
        return Files.newInputStream(root.resolve(path));
    }

    @Override
    public OutputStream writeEntry(String path) throws IOException {
        return Files.newOutputStream(root.resolve(path));
    }
}
