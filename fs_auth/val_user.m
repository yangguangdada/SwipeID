function scores = val_user(positive_name, model_path, val_path)
    % positive_name 正类名称
    % model_path svm模型路径
    % val_path 测试数据集路径
    % scores 每个测试样本的分数
    model_name = [model_path positive_name '_model.mat'];
    % 加载模型 模型变量名svm_model
    load(model_name);

    % 加载测试数据集
    val_ones = {};
    val_names = dir([val_path '*.wav']);
    val_ones = Feature_extra(val_names, val_ones, val_path, positive_name);
    val_ones = cell2mat(val_ones(:, 1:99));

    % 进行预测
    [~, scores] = predict(svm_model, val_ones);

