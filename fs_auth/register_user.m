function success = register_user(positive_name, train_path, save_path)
    % positive_name 注册用户姓名
    % train_path 训练数据目录路径   正类音频命名为 positive_name_时间戳.wav
    % 模型保存路径
    % return success true代码注册成功
    success = false;
    % 获取训练目录下的所有音频文件名
    train_names = dir([train_path '*.wav']);
    train_Ones = {};
    % 得到用于训练的数据的Table
    %如果音频文件名中含有mobile_name(正类)，那么feature_vector(100)=1,否则为（-1）
    train_Ones = Feature_extra(train_names, train_Ones, train_path, positive_name);
    % train_Ones = cell2table(train_Ones);
    X = train_Ones(:,1:99);
    Y = train_Ones(:,100);
    X = cell2mat(X);
    Y= cell2mat(Y);

    % 训练单分类svm
    svm_model = fitcsvm(X,Y,'KernelFunction','gaussian', ...
    'KernelScale', 'auto', ...
    'Standardize', true, ...
    'OutlierFraction', 0.01);

    % 保存模型svm
    if exist(save_path,'dir') == 0
        mkdir(save_path);
    end 
    model_name = [save_path positive_name '_model.mat'];
    save(model_name, 'svm_model', "X", 'Y');
    success = true;





