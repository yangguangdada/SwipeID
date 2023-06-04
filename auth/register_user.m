function success = register_user(username, train_path, save_path)
    % user_name 注册用户姓名
    % train_path 训练数据目录路径   正类音频命名为 username_时间戳.wav  data/train
    % save_path模型保存路径                 models/
    % return success true代码注册成功
    % 获取训练目录下的所有音频文件名
    train_path = fullfile(train_path,username,'\');
    save_path = fullfile(save_path,username,'\');
    train_wavs = dir([train_path  '*.wav']);
    features_wav = {};
    % 得到用于训练的数据的Table
    %如果音频文件名中含有mobile_name(正类)，那么feature_vector(100)=1,否则为（-1）
    features_wav = Feature_extra(train_wavs, features_wav, train_path, username); 
    % train_Ones = cell2table(train_Ones);
    %这些地方需要修改！！！！！！！！！！！
    X = features_wav(:,1:99);
    Y = features_wav(:,100);
    X = cell2mat(X);
    Y = cell2mat(Y);
    % 训练svm
    svm_model_0 = fitcsvm(X,Y,'KernelFunction','gaussian', 'KernelScale', 'auto', ...
                'Standardize', true,'OutlierFraction', 0.04);
    % 保存模型svm
    disp(exist(save_path,'dir'));
    if exist(save_path,'dir') == 0
        mkdir(save_path);
    end
    model_name = [save_path username '_model_0.mat'];
    save(model_name, 'svm_model_0', "X", 'Y');
    
    % 获取行为特征数据
%     train_swipes = dir([train_path '*.txt']);
%     features_swipe = csvread([train_path train_swipes.name]);
%     Y = ones(size(features_swipe,1),1);
%     svm_model_1 = fitcsvm(features_swipe, Y, 'KernelFunction','rbf',...
%                 'BoxConstraint', 1, 'Standardize', true,...
%                 'OutlierFraction', 0.04);
%     model_name = [save_path username '_model_1.mat'];
%     save(model_name,'svm_model_1',"features_swipe",'Y');
    success = 1;
end




