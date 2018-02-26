// IVendingMachineService.aidl
package kr.or.payot.poin;

import android.bluetooth.BluetoothDevice;

// Declare any non-default types here with import statements

interface IVendingMachineService {

    void connect(in BluetoothDevice device);

    void insertCoin(int coin);

    void disconnect();
}
