function denoise(filename, path)
    % filename 音频文件名
    % path 路径名
    % 去噪音频被保存在denoise子目录下 命名 filename-Denoise.wav
    load highfir.mat highfir
    
    basepath = path;  % 音频文件路径
    one_path = [basepath 'enhance\' ]; 
    one_Denoise_path = [basepath 'denoise\'];    % 保存路径
    if exist(one_path, 'dir') ==0
        mkdir(one_path);
    end
    if exist(one_Denoise_path, 'dir') ==0
        mkdir(one_Denoise_path);
    end
    % names = dir([basepath '*.wav']);
    
    % 获取不含后缀的文件名
    name = filename(1:end-4);

    file_wav = [basepath name '.wav'];
    file_filter = [one_path name '-filter.wav'];
    file_enhance = [one_path name '-Ehance.wav'];
    file_denoise = [one_Denoise_path name '-Denoise.wav'];
    
    [Y0,Fs]=audioread(file_wav);  %读取音频文件。波数据读入Y1中，波形的幅度范围在[-1, 1]。Fs存的是采样率，单位 Hz 
    oy = Y0;
    
    % 1. 去除低频信号,并消除滤波器带来的延迟
    Y0 = filter(highfir,Y0);
    delay = floor(mean(grpdelay(highfir)));
    Y0(1:delay) = [];
    audiowrite(file_filter, Y0, Fs);    %得到滤波后的文件
    
    
    % 2. 进行多频带频谱减法
    Y1=audioread(file_filter);  
    info1 = audioinfo(file_filter);     
    mband(file_filter, file_enhance,6,'log');
    
    % 3. 小波去噪，除去参与噪声
    lev=6;
    Ceng=3;
    wname = 'db3';
    Y2=audioread(file_enhance);  
    info2 = audioinfo(file_enhance);
    w=modwt(Y2, lev,wname);    %使用db3小波将Fise降到6级，分解
    XD=zeros(Ceng,length(Y2(:)));
    for j=1:Ceng
        XD(j,:)=wden(w(j,:),'modwtsqtwolog','s','mln',lev,wname);%固定阈值去噪处理后的信号序列
    end
    Y3=imodwt(XD,wname);    %重构
    audiowrite(file_denoise, Y3, Fs);
    
    toc;
    disp(['运行时间: ',num2str(toc)]);
    end
