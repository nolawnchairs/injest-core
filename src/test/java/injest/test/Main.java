/*
 * Injest - https://injest.io
 *
 * Copyright (c) 2019.
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * Last Modified: 4/11/19 9:01 AM
 */

package injest.test;

import io.injest.core.annotations.directives.PackageRoot;
import io.injest.core.InjestApplication;
import io.injest.core.boot.RestApplication;
import io.injest.core.util.DeploymentMode;

@PackageRoot("injest.test")
public class Main extends InjestApplication {

    public static void main(String[] args) {

        RestApplication.create()
                .setDeploymentMode(DeploymentMode.DEVELOPMENT)
                .setMainClass(Main.class)
                .setPort(17745)
                .setHost("")
                .build()
                .start();
    }
}
