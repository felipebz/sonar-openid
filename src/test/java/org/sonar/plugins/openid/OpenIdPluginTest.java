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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.Plugin;
import org.sonar.api.SonarQubeVersion;
import org.sonar.api.config.Settings;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@SuppressWarnings("unchecked")
public class OpenIdPluginTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void enable_extensions_if_openid_realm_is_enabled() {
    Settings settings = new Settings()
        .setProperty("sonar.security.realm", "openid")
        .setProperty("sonar.authenticator.createUsers", "true");
    List<Class<?>> extensions = (List<Class<?>>) new OpenIdPlugin.Extensions(settings).provide();

    assertThat(extensions).hasSize(6);
    assertThat(extensions).doesNotHaveDuplicates();
    assertThat(extensions).contains(OpenIdAuthenticationFilter.class);
  }

  @Test
  public void property_createUsers_must_be_true() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Property sonar.authenticator.createUsers must be set to true");

    Settings settings = new Settings()
        .setProperty("sonar.security.realm", "openid")
        .setProperty("sonar.authenticator.createUsers", "false");

    new OpenIdPlugin.Extensions(settings).provide();
  }

  @Test
  public void disable_extensions_if_default_realm() {
    Settings settings = new Settings();
    List<Class<?>> extensions = (List<Class<?>>) new OpenIdPlugin.Extensions(settings).provide();

    assertThat(extensions).isEmpty();
  }

  @Test
  public void disable_extensions_if_openid_realm_is_disabled() {
    Settings settings = new Settings().setProperty("sonar.security.realm", "LDAP");
    List<Class<?>> extensions = (List<Class<?>>) new OpenIdPlugin.Extensions(settings).provide();

    assertThat(extensions).isEmpty();
  }

  @Test
  public void getExtensions() {
    Plugin.Context context = new Plugin.Context(SonarQubeVersion.V5_6);
    new OpenIdPlugin().define(context);
    assertThat(context.getExtensions()).containsExactly(OpenIdPlugin.Extensions.class);
  }

  @Test
  public void validationFilterMustBeDeclaredBeforeAuthenticationFilter() {
    // else the sonar.forceAuthentication mode is not supported
    Settings settings = new Settings()
        .setProperty("sonar.security.realm", "openid")
        .setProperty("sonar.authenticator.createUsers", "true");
    List<Class<?>> extensions = (List<Class<?>>) new OpenIdPlugin.Extensions(settings).provide();

    assertThat(extensions.indexOf(OpenIdValidationFilter.class)).isLessThan(extensions.indexOf(OpenIdAuthenticationFilter.class));
  }
}
