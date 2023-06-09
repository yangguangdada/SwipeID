function scores = valtxt(val_path, model_path)
    % 加载模型
    load (model_path);
    % 加载行为特征测试集
    val_swipes = dir([val_path '*.txt']);
    % 进行模型预测
    features_swipe = csvread(fullfile(val_path, val_swipes.name));
    idx = any(isnan(features_swipe),2);
    features_swipe = features_swipe(~idx, :);
    [~,scores] = predict(svm_model_1, features_swipe);
end