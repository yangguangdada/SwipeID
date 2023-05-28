clc;
clear;
close all;
load highfir.mat highfir

mobile_name = 'yjh';

tic;
basepath = 'data\train\';  % 音频文件路径
one_path = [basepath 'enhance\' ]; 
one_Denoise_path = [basepath '\denoise\'];    % 保存路径
if exist(one_path, 'dir') ==0
    mkdir(one_path);
end
if exist(one_Denoise_path, 'dir') ==0
    mkdir(one_Denoise_path);
end

% img_path = 'C:\2021-11-01-data\spectrum_image\';    
names = dir([basepath '*.wav']);
% 截止频率为2200Hz的高通滤波器


for i = 1:length(names)
    % 获取不含后缀的文件名
    name = names(i).name;
    name = name(1:end-4);
    
    file_wav = [basepath name '.wav'];
    file_filter = [one_path name '-filter.wav'];
    file_enhance = [one_path name '-Ehance.wav'];
    file_denoise = [one_Denoise_path name '-Denoise.wav'];

    [Y0,Fs]=audioread(file_wav);  %读取音频文件。波数据读入Y1中，波形的幅度范围在[-1, 1]。Fs存的是采样率，单位 Hz 
    oy = Y0;

% 滤波，2.2kHz-22kHz
%     % 单边傅里叶变换，得到频率
%     L = length(Y1(:));
%     NFFT=L;
%     Y=fft(Y1,NFFT)/L;       % Y为对应频率的幅值  
%     f=Fs/2*linspace(0,1,NFFT/2+1);      % f为频率
%     figure(3)
%     subplot(211)
%     plot(f,2*abs(Y(1:NFFT/2+1)));
 
    % 1. 去除低频信号,并消除滤波器带来的延迟
    Y0 = filter(highfir,Y0);
    delay = floor(mean(grpdelay(highfir)));
    Y0(1:delay) = [];
    audiowrite(file_filter, Y0, Fs);    %得到滤波后的文件
%     L = length(voice(:));
%     NFFT=L;
%     Y=fft(voice,NFFT)/L;       % Y为对应频率的幅值  
%     f=Fs/2*linspace(0,1,NFFT/2+1);      % f为频率
%     subplot(212)
%     plot(f,2*abs(Y(1:NFFT/2+1))/L) 


    % 2. 进行多频带频谱减法
    Y1=audioread(file_filter);  
    info1 = audioinfo(file_filter);     
    mband(file_filter, file_enhance,6,'log');

    % 3. 小波去噪，除去参与噪声
    Y2=audioread(file_enhance);  
    info2 = audioinfo(file_enhance);
   
    lev=6;
    Ceng=3;
    wname = 'db3';
    w=modwt(Y2, lev,wname);    %使用db3小波将Fise降到6级，分解
    XD=zeros(Ceng,length(Y2(:)));
    for j=1:Ceng
        XD(j,:)=wden(w(j,:),'modwtsqtwolog','s','mln',lev,wname);%固定阈值去噪处理后的信号序列
    end
    Y3=imodwt(XD,wname);    %重构
    audiowrite(file_denoise, Y3, Fs);

    toc;
    disp(['运行时间: ',num2str(toc)]);
%     % 画波形图
%     Ythe=0.5;
%     t = 0:seconds(1/Fs):seconds(info1.Duration);
%     t = t(1:end-1);
%     figure(1)
%     subplot(2,2,1)
%     plot(t, Y1)          %画Y1波形图
%     ylim([-Ythe Ythe])
%     xlabel('Time');ylabel('Audio Signal');title('原始语音信号')
%     t = 0:seconds(1/Fs):seconds(info2.Duration);
%     t = t(1:end-1);
%     subplot(2,2,2)
%     plot(t, Y2)          %画Y2波形图
%     ylim([-Ythe Ythe])
%     xlabel('Time');ylabel('Audio Signal');title('谱减法后语音信号')
%     subplot(2,2,3)
%     plot(t, Y3)          %画Y3波形图
%     ylim([-Ythe Ythe])
%     xlabel('Time');ylabel('Audio Signal');title('降噪后语音信号')

    
    % 画时频图
    % Le=0; Ri=22;
    % figure(2)
    % subplot(1,1,1)
    % spectrogram(oy,256,128,256,Fs,'yaxis');
    % ylim([Le Ri]); colormap hot;
    % xlabel('时间(s)');ylabel('频率(KHz)');title('原始语谱图')
    % figname = [one_Denoise_path name '1.fig'];
    % savefig(figname)
    % subplot(1,1,1)
    % spectrogram(Y2,256,128,256,Fs,'yaxis');
    % ylim([Le Ri]); colormap hot;
    % xlabel('时间(s)');ylabel('频率(KHz)');title('谱减法后语谱图')
    % figname = [one_Denoise_path name '2.fig'];
    % savefig(figname)
    % subplot(1,1,1)
    % spectrogram(Y3,256,128,256,Fs,'yaxis');
    % ylim([Le Ri]); colormap hot;
    % xlabel('时间(s)');ylabel('频率(KHz)');title('降噪后语谱图')
    % figname = [one_Denoise_path name '3.fig'];
    % savefig(figname)
end
