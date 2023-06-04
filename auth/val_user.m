function result = val_user(username,val_path,model_path)
    % positive_name 正类名称
    % model_path svm模型路径
    % val_path 测试数据集路径  data/val
    % scores 每个测试样本的分数
    model_path = fullfile(model_path,username,'\');
    model_name_0 = [username '_model_0.mat'];
    model_name_1 = [username '_model_1.mat'];
    model_path_0 = fullfile(model_path,model_name_0);
    model_path_1 = fullfile(model_path,model_name_1);
    % 加载模型 模型变量名svm_model
    load(model_path_0);
    load(model_path_1);
    % 查找最新的验证集
    val_path = fullfile(val_path, username,'\');
    dir_list = dir(val_path);
    dir_list = dir_list(~ismember({dir_list.name},{'.','..'}));
    last_dir = dir_list(size(dir_list,1)).name;
    val_path = fullfile(val_path,last_dir,'\');
    % 加载音频测试数据集
    features_wav = {};
    val_wavs = dir([val_path '*.wav']);
    features_wav = Feature_extra(val_wavs, features_wav, val_path, username);
    X = cell2mat(features_wav(:, 1:99));
    % 进行预测
    [~, scores_0] = predict(svm_model_0, X);
    % 加载行为特征测试集
    val_swipes = dir([val_path '*.txt']);
    features_swipe = csvread([val_path val_swipes.name]);
    [~, scores_1] = predict(svm_model_1, features_swipe);
    scores_0 = normalize(scores_0,'zscore');
    scores_1 = normalize(scores_1,'zscore');
    scores = (scores_0 + scores_1)/2;
    threshold = 0.2;
    if((sum(scores>0)/size(scores,1))>threshold)
        result=1;
    else 
        result=0;
    end
    disp(scores_0);
    disp(scores_1);
end

