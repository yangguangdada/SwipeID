function res = traintxt(train_path, save_path)
    % 获取行为特征数据
    res = 0;
    train_swipes = dir([train_path '*.txt']);
    features_aug = dataAugBehave(train_swipes.name, train_path);
    filepath = fullfile(train_path, train_swipes.name);
    csvwrite(filepath,features_aug);
    Y = ones(size(features_aug,1),1);
    svm_model_1 = fitcsvm(features_aug, Y, 'KernelFunction','rbf',...
                'BoxConstraint', 1, 'Standardize', true,...
                'KernelScale', 'auto','OutlierFraction', 0.04);
    model_name = fullfile(save_path, 'model_1.mat');
    if(~exist(save_path,'dir'))
        mkdir(save_path);
    end
    features_txt = features_aug;
    save(model_name,'svm_model_1',"features_txt",'Y');
    res = 1;
end
