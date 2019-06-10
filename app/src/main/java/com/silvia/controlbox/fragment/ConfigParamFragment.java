package com.silvia.controlbox.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.silvia.controlbox.R;
import com.silvia.controlbox.utils.Common;
import com.silvia.controlbox.utils.SpWrapper;
import com.silvia.controlbox.utils.ToastUtil;

/**
 * @file ConfigParamFragment
 * 配置参数页面
 * Created by Silvia_cooper on 2018/12/11.
 */
public class ConfigParamFragment extends Fragment {

    private EditText mHostIp;
    private EditText mHostPort;
    private EditText mWorkTime;
    private EditText mTimeAcquisition;
    private EditText mTimeReport;
    private EditText mInstallMethod;
    private Button mSaveParam;
    private View view;
    private Context mContext;

    public String hostIp="180.101.147.115";
    public int hostPort=5683;
    public int workTime=3;
    public int timingAcquisition=2;
    public int timingReport=24;
    public int installMethod=2;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_configparam, container, false);
        mContext=this.getActivity();
        initView();
        initData();
        /*mHostIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFloatPop();
            }
        });*/
        return view;
    }



    public void initView(){
        mHostIp=view.findViewById(R.id.edit_host_ip);
        mHostPort=view.findViewById(R.id.edit_host_port);
        mWorkTime=view.findViewById(R.id.edit_work_time);
        mTimeAcquisition=view.findViewById(R.id.edit_timing_acquisition);
        mTimeReport=view.findViewById(R.id.edit_timing_report);
        mInstallMethod=view.findViewById(R.id.edit_install_method);
        mSaveParam=view.findViewById(R.id.button_save_param);

        mSaveParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(Common.Log,"点击了保存参数按钮");
                SaveSpParam();
                ToastUtil.showShortToast(getString(R.string.toast_save_param));
            }
        });
    }

    public void SaveSpParam(){
        String eHostIp=mHostIp.getText().toString();
        String eHostPort=mHostPort.getText().toString();
        String eDeviceTimingAcquisition=mTimeAcquisition.getText().toString();
        String eDeviceTimingReport=mTimeReport.getText().toString();
        String eDeviceWorkTime=mWorkTime.getText().toString();
        String eDeviceInstallMethod=mInstallMethod.getText().toString();
        if (eHostIp!=null&&!eHostIp.equals("")){
            SpWrapper.setHost(mContext,eHostIp);
        }else {
            ToastUtil.showShortToast(getString(R.string.toast_host));
        }

        if (eHostPort!=null&&!eHostPort.equals("")){
            SpWrapper.setHostPort(mContext,Integer.parseInt(mHostPort.getText().toString()));
        }else{
            ToastUtil.showShortToast(getString(R.string.toast_host_port));
        }

        if (eDeviceTimingAcquisition!=null&&!eDeviceTimingAcquisition.equals("")){
            SpWrapper.setDeviceTimingAcquisition(mContext,Integer.parseInt(mTimeAcquisition.getText().toString()));
        }else{
            ToastUtil.showShortToast(getString(R.string.toast_timing_accquisition));
        }

        if (eDeviceTimingReport!=null&&!eDeviceTimingReport.equals("")){
            SpWrapper.setDeviceTimingReport(mContext,Integer.parseInt(mTimeReport.getText().toString()));
        }else{
            ToastUtil.showShortToast(getString(R.string.toast_timing_report));
        }

        if (eDeviceWorkTime!=null&&!eDeviceWorkTime.equals("")){
            SpWrapper.setDeviceWorkTime(mContext,Integer.parseInt(mWorkTime.getText().toString()));
        }else{
            ToastUtil.showShortToast(getString(R.string.toast_work_time));
        }

        if (eDeviceInstallMethod!=null&&!eDeviceInstallMethod.equals("")){
            SpWrapper.setDeviceWorkTime(mContext,Integer.parseInt(mInstallMethod.getText().toString()));
        }else{
            ToastUtil.showShortToast(getString(R.string.toast_install_method));
        }



    }
    private void initData() {
        String sHostIp=SpWrapper.getHost(mContext,null);
        int sHostPort=SpWrapper.getHostPort(mContext,-1);
        int sDeviceTimingAcquisition=SpWrapper.getDeviceTimingAcquisition(mContext,-1);
        int sDeviceTimingReport=SpWrapper.getDeviceTimingReport(mContext,-1);
        int sDeviceWorkTime=SpWrapper.getDeviceWorkTime(mContext,-1);
        int sDeviceInstallMethod=SpWrapper.getDeviceInstallMethod(mContext,-1);
        if (sHostIp==null){
            mHostIp.setText(hostIp);
            SpWrapper.setHost(mContext,hostIp);
        }else {
            mHostIp.setText(sHostIp);
        }

        if (sHostPort==-1){
            mHostPort.setText(hostPort+"");
            SpWrapper.setHostPort(mContext,hostPort);
        }else{
            mHostPort.setText(sHostPort+"");
        }

        if (sDeviceTimingAcquisition==-1){
            mTimeAcquisition.setText(timingAcquisition+"");
            SpWrapper.setDeviceTimingAcquisition(mContext,timingAcquisition);
        }else {
            mTimeAcquisition.setText(sHostPort+"");
        }

        if (sDeviceWorkTime==-1){
            mWorkTime.setText(workTime+"");
            SpWrapper.setDeviceWorkTime(mContext,workTime);
        }else {
            mWorkTime.setText(sDeviceWorkTime+"");
        }

        if (sDeviceTimingReport==-1){
            mTimeReport.setText(timingReport+"");
            SpWrapper.setDeviceTimingReport(mContext,timingReport);
        }else {
            mTimeReport.setText(sDeviceTimingReport+"");
        }

        if (sDeviceInstallMethod==-1){
            mInstallMethod.setText(installMethod+"");
            SpWrapper.setDeviceInstallMethod(mContext,installMethod);
        }else {
            mInstallMethod.setText(sDeviceInstallMethod+"");
        }
    }


}
