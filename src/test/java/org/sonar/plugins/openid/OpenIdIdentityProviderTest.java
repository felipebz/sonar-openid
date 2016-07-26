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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.sonar.api.config.Settings;

public class OpenIdIdentityProviderTest {
  
  Settings settings = new Settings();
  OpenIdClient openIdClient = mock(OpenIdClient.class);
  OpenIdIdentityProvider underTest = new OpenIdIdentityProvider(settings, openIdClient);

  @Test
  public void check_fields() throws Exception {
    assertThat(underTest.getKey()).isEqualTo("openid");
    assertThat(underTest.getName()).isEqualTo("OpenID");
    assertThat(underTest.getDisplay().getIconPath()).isEqualTo("/static/openid/openid.png");
    assertThat(underTest.getDisplay().getBackgroundColor()).isEqualTo("#555555");
  }
  
}
