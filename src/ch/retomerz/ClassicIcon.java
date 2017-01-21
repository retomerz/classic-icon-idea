/**
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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.io.File;

public class ClassicIcon extends AbstractProjectComponent {

  private static Image img;
  private static Image imgBig;
  private static Image imgCustom;

  public ClassicIcon(Project project) {
    super(project);
  }

  @Override
  public void projectOpened() {
    String iconName = getIconName();
    if (imgCustom == null) {
      String custom = System.getProperty("classic.icon");
      if (!StringUtil.isEmptyOrSpaces(custom)) {
        File file = new File(custom);
        if (file.exists()) {
          if (file.isFile()) {
            if (file.canRead()) {
              ImageIcon icon = new ImageIcon(file.getPath());
              if (MediaTracker.ERRORED == icon.getImageLoadStatus()) {
                showWarning(String.format("File '%s' is not a image (for example a .png). Please fix 'classic.icon' (in IDEA_HOME/bin/idea.properties)", file));
              } else {
                imgCustom = icon.getImage();
              }
            } else {
              showWarning(String.format("File '%s' is not readable. Please fix 'classic.icon' (in IDEA_HOME/bin/idea.properties)", file));
            }
          } else {
            showWarning(String.format("Path '%s' is not a file. Please fix 'classic.icon' (in IDEA_HOME/bin/idea.properties)", file));
          }
        } else {
          showWarning(String.format("File '%s' does not exists. Please fix 'classic.icon' (in IDEA_HOME/bin/idea.properties)", file));
        }
      }
    }
    for (Frame frame : JFrame.getFrames()) {
      Image use;
      if (imgCustom != null) {
        use = imgCustom;
      } else {
        Image org = frame.getIconImage();
        if (org == null || org.getWidth(null) == 32) {
          if (img == null) {
            img = new ImageIcon(getClass().getResource("/ch/retomerz/" + iconName + ".png")).getImage();
          }
          use = img;
        } else {
          if (imgBig == null) {
            imgBig = new ImageIcon(getClass().getResource("/ch/retomerz/" + iconName + "_128.png")).getImage();
          }
          use = imgBig;
        }
      }
      frame.setIconImage(use);
    }
  }

  private void showWarning(String message) {
    if (SystemInfo.isWindows) {
      message += "<br><br>" + "Note that you need to use '\\\\' as file separator in idea.properties (and not only a single '\\')";
    }
    new Notification(
            "Classic Icon",
            "Classic Icon",
            message,
            NotificationType.WARNING
    ).setImportant(false).notify(myProject);
  }

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
}
