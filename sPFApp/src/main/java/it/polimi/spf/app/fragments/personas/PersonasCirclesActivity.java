/* 
 * Copyright 2014 Jacopo Aliprandi, Dario Archetti
 * Copyright 2015 Stefano Cappa
 *
 * This file is part of SPF.
 *
 * SPF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * SPF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SPF.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package it.polimi.spf.app.fragments.personas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.polimi.spf.app.R;
import it.polimi.spf.app.ToolbarActivity;
import it.polimi.spf.framework.profile.SPFPersona;

public class PersonasCirclesActivity extends ToolbarActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona_circles);

        ButterKnife.bind(this);

        this.setupToolbarWithHomeIcon(toolbar, R.string.title_activity_persona_circle, R.color.toolbar_text_color);

        SPFPersona persona = getIntent().getParcelableExtra("persona");
        PersonasCirclesFragment fragment = PersonasCirclesFragment.newInstance(persona);
        getFragmentManager().beginTransaction().replace(R.id.activity_persona_circles_container, fragment).commit();
    }

    @Override
    protected void setupToolbarWithHomeIcon(Toolbar toolbar, int titleStringId, int colorId) {
        super.setupToolbarWithHomeIcon(toolbar, titleStringId, colorId);
    }

    public static void start(Activity callingActivity, SPFPersona persona) {
        Intent intent = new Intent(callingActivity, PersonasCirclesActivity.class);
        intent.putExtra("persona", persona);
        callingActivity.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
