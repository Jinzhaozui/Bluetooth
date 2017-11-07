package com.hexing.cardReaderBluetooth.api;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.acs.bluetooth.Acr3901us1Reader;
import com.acs.bluetooth.BluetoothReader;
import com.acs.bluetooth.BluetoothReaderGattCallback;
import com.acs.bluetooth.BluetoothReaderManager;
import com.google.gson.Gson;
import com.hexing.cardReaderBluetooth.CardUtils;
import com.hexing.cardReaderBluetooth.HexCallback;
import com.hexing.cardReaderBluetooth.HexCardType;
import com.hexing.cardReaderBluetooth.HexError;
import com.hexing.cardReaderBluetooth.SharedPreferencesTool;
import com.hexing.cardReaderBluetooth.bean.CardReadBean;
import com.hexing.cardReaderBluetooth.bean.CardWriteBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HEC271
 * on 2017/6/14.
 */

public class HexReaderClient {
    private String deviceName;
    private String deviceAddress;
    private Context context;
    private HexCallback callback;
    private boolean isDebug = false; //测试
    private static volatile HexReaderClient instance;
    private BluetoothReader mBluetoothReader;
    private BluetoothReaderManager mBluetoothReaderManager;
    private BluetoothReaderGattCallback mGattCallback;
    /* Bluetooth GATT client. */
    private BluetoothGatt mBluetoothGatt;
    private int cardType = 0;
    private StringBuilder result = new StringBuilder(); //命令接收返回数据
    private int commandSize = 0;
    public static final String TAG = HexReaderClient.class.getSimpleName();
    private String COMMAND_TYPE;
    private static CardWriteBean cardWriteBean;
    private int errNumbr = 0;
    private boolean isCardReady = false;

    public static HexReaderClient getInstance() {
        if (instance == null) {
            synchronized (HexReaderClient.class) {
                if (instance == null) {
                    instance = new HexReaderClient();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化 client
     *
     * @param context context
     */
    public void init(@NonNull Context context) {
        init(context, null);
    }

    /**
     * 初始化 client
     *
     * @param context      上下文
     * @param paraCallback callback
     */
    public void init(@NonNull final Context context, HexCallback paraCallback) {
        this.context = context;
        this.callback = paraCallback;
        mBluetoothReaderManager = new BluetoothReaderManager();
        mGattCallback = new BluetoothReaderGattCallback();

        mGattCallback.setOnConnectionStateChangeListener(new BluetoothReaderGattCallback.OnConnectionStateChangeListener() {
            @Override
            public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int state, int newState) {
                if (isDebug)
                    Log.e("ConnectionStateChange->", "||state1-" + state + "||state2->" + newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) { //连接成功
                    if (callback != null) {
                        callback.onConnectSuccess();
                    }
                    mBluetoothReaderManager.detectReader(mBluetoothGatt, mGattCallback); //检测reader 状态
                    Log.e("ConnectionState->", "Success");
                }
                if (newState == BluetoothProfile.STATE_DISCONNECTED) { //连接失败
                    if (callback != null) {
                        callback.onError(HexError.BLUETOOTH_CONNECT_ERROR);
                    }
                    mBluetoothReader = null;
                    if (mBluetoothGatt != null) {
                        mBluetoothGatt.close();
                        mBluetoothGatt = null;
                    }
                    Log.e("ConnectionState->", "Error");
                }
            }
        });
        mBluetoothReaderManager.setOnReaderDetectionListener(new BluetoothReaderManager.OnReaderDetectionListener() {
            @Override
            public void onReaderDetection(BluetoothReader bluetoothReader) {
                if (isDebug)
                    Log.e(TAG, "onReaderDetection");
                mBluetoothReader = bluetoothReader;
                setListener();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            setAuthentication();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }

    /**
     * 增加监听回调
     *
     * @param callback callback
     */
    public void addCallback(HexCallback callback) {
        this.callback = callback;
    }

    /**
     * 调试模式
     *
     * @param aBool bool
     */
    public void setDebugMode(Boolean aBool) {
        isDebug = aBool;
    }

    /**
     * 获取错误信息
     *
     * @param code int
     * @return Str
     */
    public String getErrorStr(int code) {
        switch (code) {
            case HexError.AUTHENTICATE_ERROR:

                break;
        }
        return "";
    }

    /**
     * 连接 蓝牙
     *
     * @param deviceName    蓝牙名称
     * @param deviceAddress 蓝牙地址
     */
    public void connect(@NonNull String deviceName, @NonNull String deviceAddress) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        connectReader();
    }

    /**
     * 读取卡数据\
     * json 数据 服务端返回
     */
    //{"read":{"offset":[32],"value":[224]}}
    //{"read":{"offset":[0],"value":[1024]}}
    public void read(String json4428, String json4442) {
        if (!verify())
            return;
        Gson gson = new Gson();
        CardReadBean readBean;
        COMMAND_TYPE = HexCardType.READ_COMMAND;
        switch (cardType) {
            case HexCardType.Card_4428.CARD_CODE:
                readBean = gson.fromJson(json4428, CardReadBean.class);
                readCard4428(readBean.getRead().getOffset().get(0), readBean.getRead().getValue().get(0));
                break;
            case HexCardType.Card_4442.CARD_CODE:
                readBean = gson.fromJson(json4442, CardReadBean.class);
                readCard4442(Integer.toHexString(readBean.getRead().getOffset().get(0)), Integer.toHexString(readBean.getRead().getValue().get(0)));
                break;
        }
    }

    /**
     * 读取 4442 卡
     *
     * @param startRead 开始
     * @param lenRead   字节长度
     */
    private void readCard4442(String startRead, String lenRead) {
        StringBuilder command = new StringBuilder();
        byte escapeCommand[];
        commandSize = 1;
        command.append(HexCardType.Card_4442.READ_COMMAND);
        command.append(startRead.length() == 1 ? "0" + startRead : startRead);
        command.append(lenRead.length() == 1 ? "0" + lenRead : lenRead);
        escapeCommand = CardUtils.getEditTextInHexBytes(command.toString().toUpperCase());
        boolean commandBool = mBluetoothReader.transmitApdu(escapeCommand);
        if (isDebug)
            Log.e(TAG, "command||" + command.toString().toUpperCase() + "||发送结果||" + commandBool);
    }

    /**
     * 读取 4428 卡
     */
    private void readCard4428(int startPos, int lenReadPara) {
        byte escapeCommand[];
        List<String> arrayCommand = getCommand(startPos, lenReadPara);
        if (arrayCommand != null && arrayCommand.size() > 0) {
            commandSize = arrayCommand.size();
            for (int m = 0; m < arrayCommand.size(); m++) {
                String newCommand = HexCardType.Card_4428.READ_COMMAND + arrayCommand.get(m);
                escapeCommand = CardUtils.getEditTextInHexBytes(newCommand);
                boolean commandBool = mBluetoothReader.transmitApdu(escapeCommand);
                if (isDebug)
                    Log.e(TAG, "command4428读卡||" + newCommand + "||发送结果=" + commandBool);
            }
        }
    }

    /**
     * 写卡数据 API
     *
     * @param json json
     */
    public void write(String json) {
        if (!verify())
            return;
        COMMAND_TYPE = HexCardType.VERIFY_COMMAND_CARD;
        Gson gson = new Gson();
        cardWriteBean = gson.fromJson(json, CardWriteBean.class);
        switch (cardType) {
            case HexCardType.Card_4428.CARD_CODE:
                readCard4428(cardWriteBean.getVerify().getOffset().get(0), cardWriteBean.getVerify().getValue().get(0).length() / 2);
                break;
            case HexCardType.Card_4442.CARD_CODE:
                readCard4442(cardWriteBean.getVerify().getOffset().get(0).toString(), cardWriteBean.getVerify().getValue().get(0));
                break;
        }
    }

    /**
     * 校验 卡号  第一步
     *
     * @param readCardNum 硬件返回的卡号
     */
    private void writeVerifyCard(String readCardNum) {
        if (readCardNum != null && cardWriteBean != null && readCardNum.endsWith(HexCardType.COMMAND_SUCCESS)) {
            Log.e(TAG, "writeVerifyCard->" + readCardNum + "||web card=" + cardWriteBean.getVerify().getValue().get(0));
            if (readCardNum.equals(cardWriteBean.getVerify().getValue().get(0)))  //暂时不进行匹配卡号
            {
            }
            //卡号校验成功
            if (isDebug)
                Log.e(TAG, "卡号校验成功");
            StringBuilder newCommand = new StringBuilder();
            byte[] command;
            switch (cardType) {
                case HexCardType.Card_4428.CARD_CODE:
                    newCommand.append(HexCardType.Card_4428.VERIFY_COMMAND);
                    break;
                case HexCardType.Card_4442.CARD_CODE:
                    newCommand.append(HexCardType.Card_4442.VERIFY_COMMAND);
                    break;
            }
            newCommand.append(cardWriteBean.getOldPassword());
            if (isDebug)
                Log.e(TAG, "校验密码命令||" + newCommand.toString());
            command = CardUtils.getEditTextInHexBytes(newCommand.toString());
            if (command != null) {
                if (mBluetoothReader.transmitApdu(command)) {
                    COMMAND_TYPE = HexCardType.VERIFY_COMMAND_PASSWORD;
                } else { //命令发送失败
                    if (callback != null) {
                        callback.onError(HexError.COMMAND_ERROR);
                    }
                }
            }
        } else {
            if (isDebug) {
                Log.e(TAG, "校验卡号失败||" + readCardNum);
            }
            if (callback != null) {
                callback.onError(HexError.VERIFY_CARD_ERROR);
            }
        }
    }

    /**
     * 校验卡密码  第二步
     *
     * @param result result
     */
    private void writeVerifyPassword(String result) {
        switch (cardType) {
            case HexCardType.Card_4428.CARD_CODE:
                if (result != null && result.endsWith(HexCardType.Card_4428.VERIFY_PASSWORD_SUCCESS)) {
                    //密码校验成功
                    if (isDebug)
                        Log.e(TAG, "密码校验成功");
                    COMMAND_TYPE = HexCardType.BACKUP_COMMAND;
                    readCard4428(0, HexCardType.Card_4428.CARD_LEN);
                } else {
                    if (isDebug) {
                        Log.e(TAG, "密码失败");
                    }
                    if (callback != null) {
                        callback.onError(HexError.VERIFY_CARD_PASSWORD_ERROR);
                    }
                }
                break;
            case HexCardType.Card_4442.CARD_CODE:
                if (result != null && result.endsWith(HexCardType.Card_4442.VERIFY_PASSWORD_SUCCESS)) {
                    //密码校验成功
                    if (isDebug)
                        Log.e(TAG, "密码校验成功");
                    COMMAND_TYPE = HexCardType.BACKUP_COMMAND;
                    readCard4428(0, HexCardType.Card_4442.CARD_LEN);
                } else {
                    if (isDebug) {
                        Log.e(TAG, "密码失败");
                    }
                    if (callback != null) {
                        callback.onError(HexError.VERIFY_CARD_PASSWORD_ERROR);
                    }
                }
                break;
        }
    }

    //读卡备份数据
    private void writeBackup(String result) {
        if (result != null && result.endsWith(HexCardType.COMMAND_SUCCESS)) {
            result = result.replace(HexCardType.COMMAND_SUCCESS, "");
        }
        String backup = result;
        switch (cardType) {
            case HexCardType.Card_4428.CARD_CODE:
                if (isDebug)
                    Log.e(TAG, "备份数据成功");
                SharedPreferencesTool.putValue(this.context, "HexCardData", "4428_" + cardWriteBean.getVerify().getValue().get(0), backup);
                break;
            case HexCardType.Card_4442.CARD_CODE:
                if (isDebug)
                    Log.e(TAG, "备份数据成功");
                SharedPreferencesTool.putValue(this.context, "HexCardData", "4442_" + cardWriteBean.getVerify().getValue().get(0), backup);
                break;
        }
        writeCard();
    }

    /**
     * 写数据到卡上 第三步
     * 去除服务端返回的最后一项 密码串
     */
    private void writeCard() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (cardWriteBean == null) {
                    if (isDebug) {
                        Log.e(TAG, "开始写卡串数据丢失");
                    }
                    if (callback != null) {
                        callback.onError(HexError.SERVICE_ERROR);
                    }
                    return;
                }
                if (isDebug)
                    Log.e(TAG, "开始写卡串");
                List<String> writeValue = cardWriteBean.getWrite().getValue();
                List<Integer> offset = cardWriteBean.getWrite().getOffset();
                cardWriteBean.setPasswordOffset(cardWriteBean.getWrite().getOffset().get(cardWriteBean.getWrite().getOffset().size() - 1));
                cardWriteBean.setPasswordData(cardWriteBean.getWrite().getValue().get(cardWriteBean.getWrite().getValue().size() - 1));
                offset.remove(offset.size() - 1);
                writeValue.remove(writeValue.size() - 1);
                List<Integer> mWriteValueLenList = new ArrayList<>();
                for (String str : writeValue) {
                    mWriteValueLenList.add(str.length() / 2);
                }
                StringBuilder command = new StringBuilder();
                String baseCommand = "";
                switch (cardType) {
                    case HexCardType.Card_4428.CARD_CODE:
                        baseCommand = HexCardType.Card_4428.WRITE_COMMAND;
                        break;
                    case HexCardType.Card_4442.CARD_CODE:
                        baseCommand = HexCardType.Card_4442.WRITE_COMMAND;
                        break;
                }
                COMMAND_TYPE = HexCardType.WRITE_DATA_COMMAND;
                for (int m = 0; m < writeValue.size(); m++) {
                    command.append(baseCommand);
                    command.append(CardUtils.getHex(offset.get(m), false)); //start
                    command.append(CardUtils.getHex(mWriteValueLenList.get(m), true)); //len
                    command.append(writeValue.get(m));// data
                    boolean commandBool = mBluetoothReader.transmitApdu(CardUtils.getEditTextInHexBytes(command.toString()));
                    if (isDebug)
                        Log.e(TAG, "写卡串命令||" + command.toString() + "发送命令||" + commandBool);
                    command = new StringBuilder();
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 写密码到卡上 第四步
     */
    private void writePassword(String result) {
        if (result == null || !result.endsWith(HexCardType.COMMAND_SUCCESS)) {
            if (isDebug)
                Log.e(TAG, "写卡串失败");
            writeCard(); //重写刚才的数据
            return;
            //写卡串失败
        }
        if (isDebug)
            Log.e(TAG, "写卡串成功");
        StringBuilder command = new StringBuilder();
        switch (cardType) {
            case HexCardType.Card_4428.CARD_CODE:
                if (isDebug)
                    Log.e(TAG, "开始写密码");
                COMMAND_TYPE = HexCardType.WRITE_PASSWORD_COMMAND;
                command.append(HexCardType.Card_4428.WRITE_COMMAND);
                command.append(CardUtils.getHex(cardWriteBean.getPasswordOffset(), false));
                int dataLen = cardWriteBean.getPasswordData().length() / 2;
                command.append(CardUtils.getHex(dataLen, true));
                command.append(cardWriteBean.getPasswordData());
                byte[] bytes = CardUtils.getEditTextInHexBytes(command.toString());
                if (bytes == null) {
                    if (isDebug)
                        Log.e(TAG, "写密码命令bytes is||Null");
                    return;
                }
                Log.e(TAG, "写密码命令bytes||" + Arrays.toString(bytes));
                Boolean commandBool = mBluetoothReader.transmitApdu(bytes);
                if (isDebug)
                    Log.e(TAG, "写密码命令||" + command.toString() + "||发送命令" + commandBool);
                break;
            case HexCardType.Card_4442.CARD_CODE:
                if (isDebug)
                    Log.e(TAG, "开始写密码");
                //command.append(HexCardType.Card_4442.WRITE_DATA_COMMAND);
                writePassBack(HexCardType.COMMAND_SUCCESS);
                break;
        }
    }

    /**
     * 写密码回调函数
     *
     * @param result str
     */
    private void writePassBack(String result) {
        if (result != null && result.endsWith(HexCardType.COMMAND_SUCCESS)
                && callback != null) {
            callback.onWriteSuccess();
            if (isDebug)
                Log.e(TAG, "写密码成功");
        } else {
            if (isDebug)
                Log.e(TAG, "写密码失败-》" + result);
        }
    }

    /**
     * 获取卡片类型
     *
     * @return int 4428 或 4442
     */
    public int getCardType() {
        return cardType;
    }

    /**
     * 获取 命令 字符串数组
     *
     * @param startPos    start
     * @param lenReadPara 字节长度
     * @return data array
     */
    private List<String> getCommand(int startPos, int lenReadPara) {
        int address = startPos;
        int len = lenReadPara;
        int unit = 255;
        int MaxL = len / unit;
        int lastLen = 0;
        if (len % unit > 0) {
            MaxL += 1;
        }
        List<String> commandList = new ArrayList<>();
        String startRead, lenRead;
        for (int i = 0; i < MaxL; i++) {
            if (i == MaxL - 1 && (len % unit > 0)) {
                address += lastLen;
                startRead = CardUtils.getHex(address, false);
                lenRead = CardUtils.getHex(len % unit, true);

            } else {
                address += lastLen;
                startRead = CardUtils.getHex(address, false);
                lenRead = CardUtils.getHex(unit, true);
                lastLen = unit;
            }
            commandList.add((startRead + lenRead).toUpperCase());
            if (isDebug)
                Log.e(TAG, "Command:" + startRead + lenRead);
        }
        return commandList;
    }

    /**
     * 验证设备
     */
    private void setAuthentication() {
        Log.e("HexReader-Auth", "setAuthentication");
        final String DEFAULT_3901_MASTER_KEY = "FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF";
        byte masterKey[] = CardUtils.getEditTextInHexBytes(DEFAULT_3901_MASTER_KEY);
        if (!mBluetoothReader.authenticate(masterKey)) {
            if (callback != null) {
                callback.onError(HexError.AUTHENTICATE_ERROR);
            }
        }
    }

    /**
     * 绑定监听
     */
    private void setListener() {
        this.isCardReady = false;
        ((Acr3901us1Reader) mBluetoothReader).startBonding();
        mBluetoothReader.setOnAuthenticationCompleteListener(new BluetoothReader.OnAuthenticationCompleteListener() {
            @Override
            public void onAuthenticationComplete(BluetoothReader bluetoothReader, int i) {
                if (isDebug) {
                    Log.e("onAuthComplete->", "||state1-" + i);
                }
                isCardReady = true;
                verify();
            }

        });

        mBluetoothReader.setOnCardStatusAvailableListener(new BluetoothReader.OnCardStatusAvailableListener() {
            @Override
            public void onCardStatusAvailable(BluetoothReader bluetoothReader, int cardStatus, int errorCode) {
                if (isDebug)
                    Log.e("onCardStatusAvailable->", "||cardStatus【" + cardStatus + "】||errorCode【" + errorCode + "】");
            }
        });

        mBluetoothReader.setOnAtrAvailableListener(new BluetoothReader.OnAtrAvailableListener() {
            @Override
            public void onAtrAvailable(BluetoothReader bluetoothReader, byte[] bytes, int i) {
                String atr = CardUtils.toHexString(bytes).replace(" ", "");
                if (atr != null && atr.equals(HexCardType.Card_4442.CARD_ATR)) {
                    cardType = HexCardType.Card_4442.CARD_CODE;
                }
                if (atr != null && atr.equals(HexCardType.Card_4428.CARD_ATR)) {
                    cardType = HexCardType.Card_4428.CARD_CODE;
                }
            }
        });

        mBluetoothReader.setOnEscapeResponseAvailableListener(new BluetoothReader.OnEscapeResponseAvailableListener() {
            @Override
            public void onEscapeResponseAvailable(BluetoothReader bluetoothReader, byte[] bytes, int i) {
                Log.e(TAG, getResponseString(bytes, i));
            }
        });

        mBluetoothReader.setOnResponseApduAvailableListener(new BluetoothReader.OnResponseApduAvailableListener() {
            @Override
            public void onResponseApduAvailable(BluetoothReader bluetoothReader, byte[] bytes, int i) {
                String response = getResponseString(bytes, i).replace(" ", "");
                if (isDebug)
                    Log.e(TAG, "执行命令返回APDU->" + response);
                if (response.endsWith(HexCardType.COMMAND_SUCCESS)
                        || response.equals(HexCardType.Card_4428.VERIFY_PASSWORD_SUCCESS)
                        || response.equals(HexCardType.Card_4442.VERIFY_PASSWORD_SUCCESS)) {
                    if (commandSize > 1) {
                        response = response.substring(0, response.length() - 4);
                        result.append(response);
                        --commandSize;
                    } else {
                        result.append(response);
                        commandSize = 0;
                    }
                    if (isDebug)
                        Log.e(TAG, "数据返回size-" + commandSize + "||call-" + callback);
                    switch (COMMAND_TYPE) {
                        case HexCardType.VERIFY_COMMAND_CARD:
                            //内部验证卡号
                            writeVerifyCard(result.toString());
                            break;
                        case HexCardType.VERIFY_COMMAND_PASSWORD:
                            //内部验证密码
                            writeVerifyPassword(result.toString());
                            break;
                        case HexCardType.BACKUP_COMMAND:
                            //备份数据
                            if (commandSize == 0)
                                writeBackup(result.toString());
                            break;
                        case HexCardType.WRITE_DATA_COMMAND:
                            //写卡串
                            if (commandSize == 0)
                                writePassword(result.toString());
                            break;
                        case HexCardType.WRITE_PASSWORD_COMMAND:
                            //写密码
                            writePassBack(result.toString());
                            break;
                        case HexCardType.READ_COMMAND:
                            if (callback != null && commandSize == 0) {
                                callback.onReadSuccess(result.toString());
                                result = new StringBuilder();
                            }
                            break;
                    }
                }
                if (callback != null && i != 0) {
                    callback.onError(i);
                }
            }
        });
    }

    /* Get the Response string. */
    private String getResponseString(byte[] response, int errorCode) {
        if (errorCode == BluetoothReader.ERROR_SUCCESS) {
            if (response != null && response.length > 0) {
                return CardUtils.toHexString(response);
            }
        }
        return getErrorString(errorCode);
    }

    /* Get the Error string. */
    private String getErrorString(int errorCode) {
        if (errorCode == BluetoothReader.ERROR_SUCCESS) {
            return "";
        } else if (errorCode == BluetoothReader.ERROR_INVALID_CHECKSUM) {
            return "The checksum is invalid.";
        } else if (errorCode == BluetoothReader.ERROR_INVALID_DATA_LENGTH) {
            return "The data length is invalid.";
        } else if (errorCode == BluetoothReader.ERROR_INVALID_COMMAND) {
            return "The command is invalid.";
        } else if (errorCode == BluetoothReader.ERROR_UNKNOWN_COMMAND_ID) {
            return "The command ID is unknown.";
        } else if (errorCode == BluetoothReader.ERROR_CARD_OPERATION) {
            return "The card operation failed.";
        } else if (errorCode == BluetoothReader.ERROR_AUTHENTICATION_REQUIRED) {
            return "Authentication is required.";
        } else if (errorCode == BluetoothReader.ERROR_LOW_BATTERY) {
            return "The battery is low.";
        } else if (errorCode == BluetoothReader.ERROR_CHARACTERISTIC_NOT_FOUND) {
            return "Error characteristic is not found.";
        } else if (errorCode == BluetoothReader.ERROR_WRITE_DATA) {
            return "Write command to reader is failed.";
        } else if (errorCode == BluetoothReader.ERROR_TIMEOUT) {
            return "Timeout.";
        } else if (errorCode == BluetoothReader.ERROR_AUTHENTICATION_FAILED) {
            return "Authentication is failed.";
        } else if (errorCode == BluetoothReader.ERROR_UNDEFINED) {
            return "Undefined error.";
        } else if (errorCode == BluetoothReader.ERROR_INVALID_DATA) {
            return "Received data error.";
        } else if (errorCode == HexError.SERVICE_ERROR) {
            return "蓝牙服务失败";
        } else if (errorCode == HexError.NO_CARD_ERROR) {
            return "没有卡或不能读";
        } else if (errorCode == HexError.POWER_ON_CARD_ERROR) {
            return "上电失败";
        } else if (errorCode == HexError.CARD_NOT_READY_ERROR) {
            return "卡未初始化成功";
        } else if (errorCode == HexError.BLUETOOTH_CONNECT_ERROR) {
            return "蓝牙连接失败";
        } else if (errorCode == HexError.AUTHENTICATE_ERROR) {
            return "auth验证失败";
        } else if (errorCode == HexError.COMMAND_ERROR) {
            return "命令发送失败";
        } else if (errorCode == HexError.VERIFY_CARD_ERROR) {
            return "卡号校验失败";
        } else if (errorCode == HexError.VERIFY_CARD_PASSWORD_ERROR) {
            return "密码校验失败";
        }
        return "Unknown error.";
    }

    /**
     * 校验 蓝牙  检测卡状态 上电 读取卡类型
     *
     * @return bool
     */
    private boolean verify() {
        if (mBluetoothReader == null) {
            if (callback != null)
                callback.onError(HexError.CARD_NOT_READY_ERROR);
            return false;
        }
        boolean cardStatus = mBluetoothReader.getCardStatus(); //检测卡状态
        if (!cardStatus) {
            if (callback != null) {
                callback.onError(HexError.NO_CARD_ERROR);
            }
            return false;
        }
        if (!mBluetoothReader.powerOnCard()) { //上电
            if (callback != null) {
                callback.onError(HexError.POWER_ON_CARD_ERROR);
            }
            return false;
        }
        if (this.callback != null && this.isCardReady) {
            this.callback.onReadySuccess();
            if (this.isDebug) {
                Log.e(TAG, "Card is Ready Success");
            }
        }
        return true;
    }

    /*
     * Create a GATT connection with the reader. And detect the connected reader
     * once service list is available.
     */
    private boolean connectReader() {
        BluetoothManager bluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            if (isDebug)
                Log.w(TAG, "Unable to initialize BluetoothManager.");
            return false;
        }

        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            if (isDebug)
                Log.w(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        /*
         * Connect Device.
         */
        /* Clear old GATT connection. */
        if (mBluetoothGatt != null) {
            if (isDebug)
                Log.i(TAG, "Clear old GATT connection");
            try {
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
                mBluetoothGatt = null;
            } catch (Exception ex) {
                mBluetoothGatt = null;
            }
        }

        /* Create a new connection. */
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device == null) {
            if (isDebug)
                Log.w(TAG, "Device not found. Unable to connect.");
            return false;
        }

        /* Connect to GATT server. */
        mBluetoothGatt = device.connectGatt(this.context, false, mGattCallback);
        return true;
    }

    public void onDestroy() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
        }
        if (this.mBluetoothReader != null) {
            this.mBluetoothReader.powerOnCard();
            this.mBluetoothReader = null;
            instance = null;
            this.isCardReady = false;
        }
    }
}
