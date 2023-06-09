function res = trainwav(train_path, save_path)
    res = 0;
    train_wavs = dir([train_path  '*.wav']);  %读取所有音频文件
    % 得到用于训练的数据的Table
    %如果音频文件名中含有mobile_name(正类)，那么feature_vector(100)=1,否则为（-1）
    
    train_wavs = reshape(train_wavs, [1 size(train_wavs,1)]);
    for wav = train_wavs
        denoise(wav.name,train_path);  %去噪
    end 
    den_dirpath = fullfile(train_path, 'denoise','/');
    train_de_wavs = dir([den_dirpath '*.wav']);
    train_de_wavs = reshape(train_de_wavs, [1 size(train_de_wavs,1)]);
    for de_wav = train_de_wavs
        % dataAugWav(de_wav.name, den_dirpath);
    end
    train_de_aug_wavs = dir([den_dirpath '*.wav']);
    features_wav = zeros(size(train_de_aug_wavs,1),99); % 预先分配对象
    for idx = 1:numel(train_de_aug_wavs)
        feature_wav = Feature_extra(train_de_aug_wavs(idx).name, den_dirpath); 
        features_wav(idx,:) = feature_wav; 
    end
    Y = ones(size(features_wav,1),1);
    % 训练svm
    svm_model_0 = fitcsvm(features_wav, Y, 'KernelFunction','gaussian', 'KernelScale', 'auto', ...
                'Standardize', true,'OutlierFraction', 0.04);
    % 保存模型svm
    disp(exist(save_path,'dir'));
    if exist(save_path,'dir') == 0
        mkdir(save_path);
    end
    model_name = [save_path 'model_0.mat'];
    save(model_name, 'svm_model_0', "features_wav", 'Y');
    disp(['训练样本个数 ' num2str(size(features_wav,1)) '个']);
    res = 1;
end