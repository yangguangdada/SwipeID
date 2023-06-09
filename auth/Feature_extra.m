function Ones = Feature_extra(filename, path)
    % names 音频文件名列表 建议文件名 positive_name_时间戳.wav
    % Ones 特征矩阵
    % path 音频文件路径 
    % 根据文件名列表，提取音频的特征
    % Ones 就是最后得到的特征矩阵
    Ones = zeros(1,99);
    % 获取不含后缀的文件名
    filepath = fullfile(path, filename);
    [audioIn,fs] = audioread(filepath);

    % Level1 features
    tmp1 = GetLevel1(audioIn,fs);

    % Level2 features
    tmp2 = GetMFCC(audioIn,fs);
    Lpcc = my_lpcc(audioIn); % 12阶lpcc和1阶误差
    Lpcc = Lpcc'; %进行转置，行帧数，列系数
    [~,RastaPlp] =  rastaplp(audioIn, fs);
    RastaPlp = RastaPlp'; % 27阶RastaPlp

    % Level3 features
    Lsf = my_lsf(audioIn,fs); % 12阶lsf
    [Flut,Kurtosis,Skewness,Slope] = get_spectral_statistics(audioIn,fs); % spectral statistics 

    % 得到所有帧所有特征，一共99个特征   ！！！！！！！！！！！！！！此处需要修改成27
    Features = [tmp1 tmp2 Lpcc(:,2:12) RastaPlp(:,1:27) Lsf Kurtosis Skewness];
    % 存入一行作为样本，每一个胞元包含一个特征的特征值（向量）
    %!!!!!!!!!此处需要修改为99
    for j =1:99
        Ones(1, j) = Features(1, j);
    end
end