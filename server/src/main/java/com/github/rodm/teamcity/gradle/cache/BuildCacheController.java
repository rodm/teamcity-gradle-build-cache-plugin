/*
 * Copyright 2017 Rod MacKenzie.
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

package com.github.rodm.teamcity.gradle.cache;

import jetbrains.buildServer.controllers.BaseAjaxActionController;
import jetbrains.buildServer.web.openapi.ControllerAction;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BuildCacheController extends BaseAjaxActionController {

    public BuildCacheController(WebControllerManager controllerManager, BuildCacheServer cacheServer) {
        super(controllerManager);
        controllerManager.registerController("/admin/buildCache.html", this);
        controllerManager.registerAction(this, new ClearCacheAction(cacheServer));
    }

    static class ClearCacheAction implements ControllerAction {

        private final BuildCacheServer cacheServer;

        ClearCacheAction(BuildCacheServer cacheServer) {
            this.cacheServer = cacheServer;
        }

        @Override
        public boolean canProcess(@NotNull HttpServletRequest request) {
            return "clear".equals(request.getParameter("action"));
        }

        @Override
        public void process(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @Nullable Element ajaxResponse) {
            cacheServer.clear();
        }
    }
}
