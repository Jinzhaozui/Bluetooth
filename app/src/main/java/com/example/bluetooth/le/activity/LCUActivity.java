package com.example.bluetooth.le.activity;

import com.example.bluetooth.le.base.BaseActivity;
/**
 * Created by HEC271
 * on 2017/6/7.
 */

public class LCUActivity extends BaseActivity {
//    private FragmentManager fragmentManager = null;
//    private LcuOneFragment lcuOneFragment;
//    private LcuTwoFragment lcuTwoFragment;
//    private LcuThreeFragment lcuThreeFragment;
//    private LcuFourFragment lcuFourFragment;
//    private ViewPager viewPager;
//    private FragmentAdapter mFragmentAdapter;
//    private List<Fragment> mFragmentList = new ArrayList<>();
//
//    private BluetoothLeService mBluetoothLeService;
//    private BluetoothReceiver bluetoothReceiver;
//    private CommandUtil commandUtil;
//    private String dataType;
//    private String mDeviceAddress = "";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_lcu);
//        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        initFragment();
//        initData();
//    }
//
//    private void initFragment() {
//        lcuOneFragment = new LcuOneFragment();
//        lcuTwoFragment = new LcuTwoFragment();
//        lcuThreeFragment = new LcuThreeFragment();
//        lcuFourFragment = new LcuFourFragment();
//        mFragmentList.add(lcuOneFragment);
//        mFragmentList.add(lcuTwoFragment);
//        mFragmentList.add(lcuThreeFragment);
//        mFragmentList.add(lcuFourFragment);
//        mFragmentAdapter = new FragmentAdapter(fragmentManager, mFragmentList);
//        viewPager.setAdapter(mFragmentAdapter);
//    }
//
//    private void initData() {
//        bluetoothReceiver = new BluetoothReceiver(getApplicationContext(), uiHandler);
//        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
//        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//
//        commandUtil = new CommandUtil(uiHandler);
//        commandUtil.init();
//    }
//
//    // Code to manage Service lifecycle.
//    private final ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder service) {
//            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
//            if (!mBluetoothLeService.initialize()) {
//                Log.e("TAG", "Unable to initialize Bluetooth");
//                finish();
//            }
//            // Automatically connects to the device upon successful start-up initialization.
//            mBluetoothLeService.connect(mDeviceAddress);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            mBluetoothLeService = null;
//        }
//    };
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mBluetoothLeService != null) {
//            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
//            Log.d("TAG", "Connect request result=" + result);
//        }
//        bluetoothReceiver.register(bluetoothReceiver);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        bluetoothReceiver.unRegister(bluetoothReceiver);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unbindService(mServiceConnection);
//        mBluetoothLeService = null;
//    }
//
//    @Override
//    public void doHandler(Message msg) {
//        super.doHandler(msg);
//    }
}
