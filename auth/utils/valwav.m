function scores = valwav(val_path, model_path)
    %加载模型
    load(model_path);
    %加载验证音频
    val_wavs = dir([val_path '*.wav']);
    val_wavs = reshape(val_wavs, [1 size(val_wavs, 1)]);
    for wav = val_wavs
        denoise(wav.name,val_path);  %去噪
    end 
    den_dirpath = fullfile(val_path, 'denoise','/');
    val_de_wavs = dir([den_dirpath '*.wav']);
    features_wav = zeros(size(val_de_wavs, 1),99); % 预先分配对象
    for idx = 1:numel(val_de_wavs)
        feature_wav = Feature_extra(val_de_wavs(idx).name, den_dirpath); 
        features_wav(idx,:) = feature_wav; 
    end
    % 进行预测
    [~,scores] = predict(svm_model_0, features_wav);
end