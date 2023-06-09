function dataAugWav(filename,path)
    % 加载语音文件
    tic
    filepath = fullfile(path, filename);
    [x,fs] = audioread(filepath);
    
    % 定义添加噪声的增强器
    augmenter = audioDataAugmenter('NumAugmentations', 2,...
                'AddNoiseProbability',1,...
                'SNRRange',[0 5],...
                'TimeStretchProbability',0,...
                'PitchShiftProbability',0,...
                'VolumeControlProbability',0,...
                'TimeShiftProbability',0);

                

    % 应用增强器
    augX = augment(augmenter,x, fs);
    % 将增强后的音频文件写入磁盘
    for i = 1:size(augX,1)
        f = strcat(filename(1:end-4), '-aug',num2str(i),'.wav');
        filepath = fullfile(path, f);
        x = cell2mat(augX{i,'Audio'});
        audiowrite(filepath, x, fs);
    end
    disp([filename '   augment cost  ' num2str(toc) '  s']);
    toc
end