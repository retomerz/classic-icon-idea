/*
 * This file is part of Class Icon, licensed under the MIT License (MIT).
 * <p/>
 * Copyright (c) Reto Merz
 * Copyright (c) JetBrains s.r.o.
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ch.retomerz;

import com.intellij.ide.ApplicationLoadListener;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;

import java.awt.Image;
import java.lang.reflect.Method;
import java.util.List;

public class AppLoader implements ApplicationLoadListener {
  //@Override compatibility
  public void beforeComponentsCreated() {
  }

  @Override
  public void beforeApplicationLoaded(@NotNull Application application, @NotNull String configPath) {
    if (!SystemInfo.isMac) {
      return;
    }
    application.invokeLater(new Runnable() {
      @Override
      public void run() {
        setDockIcon();
      }
    });
  }

  private static void setDockIcon() {
    try {
      List<Image> images = ClassicIcon.loadImages(null);
      Image biggest = images.get(images.size() - 1);
      Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
      Method getApplicationMethod = applicationClass.getMethod("getApplication");
      Method setDockIconImageMethod = applicationClass.getMethod("setDockIconImage", Image.class);
      Object application = getApplicationMethod.invoke(null);
      setDockIconImageMethod.invoke(application, biggest);
    } catch (Exception e) {
      Logger.getInstance(AppLoader.class).error("Error while set Classic Icon", e);
      ClassicIcon.showWarning(null, "Error while set Classic Icon. Please find more infos in idea.log");
    }
  }
}
