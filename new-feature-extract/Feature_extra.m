function Ones = Feature_extra(names, Ones, path, positive_name)
% 根据文件名列表，提取音频的特征
% Ones 就是最后得到的特征矩阵
    for i = 1:length(names)
        % 获取不含后缀的文件名
        name = names(i).name;
        name = name(1:end-4);

        % 读取音频文件
        filename = [path name '.wav'];
        [audioIn,fs] = audioread(filename);

        % Level1 features
        tmp1 = GetLevel1(audioIn,fs);

        % Level2 features
        tmp2 = GetMFCC(audioIn,fs);
        Lpcc = my_lpcc(audioIn); % 12阶lpcc和1阶误差
        Lpcc = Lpcc';
        [~,RastaPlp] =  rastaplp(audioIn, fs);
        RastaPlp = RastaPlp'; % 27阶RastaPlp

        % Level3 features
        Lsf = my_lsf(audioIn,fs); % 12阶lsf
        [Flut,Kurtosis,Skewness,Slope] = get_spectral_statistics(audioIn,fs); % spectral statistics 

        % 得到所有帧所有特征，一共99个特征
        Features = [tmp1 tmp2 Lpcc(:,2:12) RastaPlp(:,1:27) Lsf Kurtosis Skewness];
        % 存入一行作为样本，每一个胞元包含一个特征的特征值（向量）
        for j =1:99
            Ones{i, j} = Features(1, j);
        end

        if contains(name, positive_name)
            Ones{i, 100} = 1;
        else
            Ones{i, 100} = -1;
        end 
        
    end
end