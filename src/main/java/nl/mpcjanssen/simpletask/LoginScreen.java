/**
 * This file is part of Todo.txt Touch, an Android app for managing your todo.txt file (http://todotxt.com).
 *
 * Copyright (c) 2009-2012 Todo.txt contributors (http://todotxt.com)
 *
 * LICENSE:
 *
 * Todo.txt Touch is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Todo.txt Touch is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with Todo.txt Touch.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * @author Todo.txt contributors <todotxt@yahoogroups.com>
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2009-2012 Todo.txt contributors (http://todotxt.com)
 */
package nl.mpcjanssen.simpletask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import nl.mpcjanssen.simpletask.remote.RemoteClient;


public class LoginScreen extends Activity {

    final static String TAG = LoginScreen.class.getSimpleName();

    private TodoApplication m_app;
    private BroadcastReceiver m_broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_app = (TodoApplication) getApplication();
        setTheme(m_app.getActiveTheme());
        setContentView(R.layout.login);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("nl.mpcjanssen.simpletask.ACTION_LOGIN");
        m_broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent i = new Intent(context, Simpletask.class);
                startActivity(i);
                finish();
            }
        };
        registerReceiver(m_broadcastReceiver, intentFilter);

        Button m_LoginButton = (Button) findViewById(R.id.login);
        m_LoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });



        RemoteClient remoteClient = m_app.getRemoteClientManager()
                .getRemoteClient();
        if (remoteClient.isAuthenticated()) {
            switchToTodolist();
        }
    }

    private void switchToTodolist() {
        Intent intent = new Intent(this, Simpletask.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        finishLogin();
    }

    private void finishLogin() {
        RemoteClient remoteClient = m_app.getRemoteClientManager()
                .getRemoteClient();
        remoteClient.finishLogin();
        if (remoteClient.finishLogin() && remoteClient.isAuthenticated()) {
            switchToTodolist();
            sendBroadcast(new Intent(getPackageName()+Constants.BROADCAST_START_SYNC_FROM_REMOTE));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(m_broadcastReceiver);
    }

    void startLogin() {
        final RemoteClient client = m_app.getRemoteClientManager()
                .getRemoteClient();
        client.startLogin();
    }

}
