/**
 * This file is part of ClassIcon, licensed under the MIT License (MIT).
 * <p>
 * Copyright (c) Reto Merz
 * Copyright (c) JetBrains s.r.o.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ch.retomerz;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Frame;
import java.awt.Image;

public class ClassicIcon extends AbstractProjectComponent {

  private static Image img;
  private static Image imgBig;
  private static Image imgCustom;

  public ClassicIcon(Project project) {
    super(project);
  }

  @Override
  public void projectOpened() {
    String ce = "";
    if ("Idea".equals(System.getProperty("idea.platform.prefix"))) {
      ce = "_CE";
    }
    if (imgCustom == null) {
      if (!StringUtil.isEmptyOrSpaces(System.getProperty("classic.icon"))) {
        imgCustom = new ImageIcon(System.getProperty("classic.icon")).getImage();
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
            img = new ImageIcon(getClass().getResource("/ch/retomerz/icon" + ce + ".png")).getImage();
          }
          use = img;
        } else {
          if (imgBig == null) {
            imgBig = new ImageIcon(getClass().getResource("/ch/retomerz/icon" + ce + "_128.png")).getImage();
          }
          use = imgBig;
        }
      }
      frame.setIconImage(use);
    }
  }
}
