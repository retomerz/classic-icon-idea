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

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.util.ImageLoader;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.Frame;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ClassicIcon extends AbstractProjectComponent {

  public ClassicIcon(Project project) {
    super(project);
  }

  @Override
  public void projectOpened() {
    if (SystemInfo.isMac) {
      return;
    }
    List<Image> images = loadImages(myProject);
    for (Frame frame : JFrame.getFrames()) {
      if (frame instanceof IdeFrame) {
        frame.setIconImages(images);
      }
    }
  }

  static void showWarning(@Nullable Project project, String message) {
    if (SystemInfo.isWindows) {
      message += "<br><br>" + "Note that you need to use '\\\\' as file separator in idea.properties (and not only a single '\\')";
    }
    new Notification(
            "Classic Icon",
            "Classic Icon",
            message,
            NotificationType.WARNING
    ).setImportant(false).notify(project);
  }

  @NotNull
  private static String getIconName() {
    String prefix = System.getProperty("idea.platform.prefix");
    if (prefix != null) {
      prefix = prefix.toLowerCase();
      if ("idea".equals(prefix)) {
        return "icon_CE";
      } else if (prefix.startsWith("phpstorm")) {
        return "phpstorm";
      } else if (prefix.startsWith("pycharm")) {
        return "pycharm";
      } else if (prefix.startsWith("ruby")) {
        return "rubymine";
      } else if (prefix.startsWith("webstorm")) {
        return "webstorm";
      } else if (prefix.startsWith("appcode")) {
        return "appcode";
      } else if (prefix.startsWith("clion")) {
        return "clion";
      } else if (prefix.startsWith("datagrip")) {
        return "datagrip";
      } else if (prefix.startsWith("rider")) {
        return "rider";
      }
    }
    return "icon";
  }

  @NotNull
  static List<Image> loadImages(@Nullable Project project) {
    List<Image> ret = ContainerUtil.newArrayListWithCapacity(2);
    Image custom = tryLoadCustom(project);
    if (custom != null) {
      ret.add(custom);
    } else {
      String iconName = getIconName();
      ret.add(ImageLoader.loadFromResource("/ch/retomerz/" + iconName + ".png", ClassicIcon.class));
      ret.add(ImageLoader.loadFromResource("/ch/retomerz/" + iconName + "_128.png", ClassicIcon.class));
    }
    return ret;
  }

  @Nullable
  private static Image tryLoadCustom(@Nullable Project project) {
    String custom = System.getProperty("classic.icon");
    if (StringUtil.isEmptyOrSpaces(custom)) {
      return null;
    }
    File file = new File(custom);
    if (!file.exists()) {
      showWarning(project, String.format("File '%s' does not exists. Please fix 'classic.icon' (in IDEA_HOME/bin/idea.properties)", file));
      return null;
    }
    if (!file.isFile()) {
      showWarning(project, String.format("Path '%s' is not a file. Please fix 'classic.icon' (in IDEA_HOME/bin/idea.properties)", file));
      return null;
    }
    if (!file.canRead()) {
      showWarning(project, String.format("File '%s' is not readable. Please fix 'classic.icon' (in IDEA_HOME/bin/idea.properties)", file));
      return null;
    }
    try {
      return ImageIO.read(file);
    } catch (IOException e) {
      showWarning(project, String.format("File '%s' is not a image (for example a .png). Please fix 'classic.icon' (in IDEA_HOME/bin/idea.properties)", file));
      Logger.getInstance(ClassicIcon.class).error("Could not load image " + custom, e);
      return null;
    }
  }
}
