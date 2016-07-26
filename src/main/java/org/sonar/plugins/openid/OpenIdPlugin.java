/*
 * Sonar OpenID Plugin
 * Copyright (C) 2012-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.openid;

import java.util.List;

import org.sonar.api.ExtensionProvider;
import org.sonar.api.Plugin;
import org.sonar.api.config.Settings;
import org.sonar.api.server.ServerSide;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public final class OpenIdPlugin implements Plugin {

  @Override
  public void define(Context context) {
      context.addExtension(Extensions.class);
  }

  @ServerSide
  public static final class Extensions extends ExtensionProvider {
    private Settings settings;

    public Extensions(Settings settings) {
      this.settings = settings;
    }

    @Override
    public Object provide() {
      List<Class<?>> extensions = Lists.newArrayList();
      if (isRealmEnabled()) {
        Preconditions.checkState(settings.getBoolean("sonar.authenticator.createUsers"), "Property sonar.authenticator.createUsers must be set to true.");
        extensions.add(OpenIdSecurityRealm.class);
        extensions.add(OpenIdClient.class);
        extensions.add(OpenIdAuthenticator.class);
        extensions.add(OpenIdValidationFilter.class);
        extensions.add(OpenIdAuthenticationFilter.class);
        extensions.add(OpenIdLogoutFilter.class);
      }
      return extensions;
    }

    private boolean isRealmEnabled() {
      return OpenIdSecurityRealm.KEY.equalsIgnoreCase(settings.getString("sonar.security.realm"));
    }
  }
}
