package pl.air.bluetooth_controller;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class RobotController extends AppCompatActivity {
        private ConnectedThread connectedThread;
        public Handler mHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.robot_controller);
        Button btnForward = findViewById(R.id.buttonForward);
        Button bntBackward = findViewById(R.id.buttonBackward);
        Button bntLeft = findViewById(R.id.buttonLeft);
        Button bntRight = findViewById(R.id.buttonRight);

        BluetoothSocket mSocket = SocketHandler.getSocket();
        if(mSocket != null) {
            connectedThread = new ConnectedThread(mSocket, mHandler);
            connectedThread.start();
        }



        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String forward = "F";
                connectedThread.write(forward.getBytes());
            }
        });

        bntBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String backward = "B";
                connectedThread.write(backward.getBytes());
            }
        });

        bntLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String left = "L";
                connectedThread.write(left.getBytes());
            }
        });

        bntRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String right = "R";
                connectedThread.write(right.getBytes());
            }
        });

    }


}
