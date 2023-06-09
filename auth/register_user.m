function success = register_user(username,behaviour_type, train_path, save_path)
    % user_name 注册用户姓名
    % behaviour_type 行为类型
    % train_path 训练数据目录路径   正类音频命名为 username_时间戳.wav  data/train
    % save_path模型保存路径                 models/
    % return success 1 注册成功
    % 获取训练目录下的所有音频文件名
    train_path = fullfile(train_path,username, behaviour_type, '\');
    save_path = fullfile(save_path,username, behaviour_type, '\');
    % 寻找最新的训练集
    dir_list = dir(train_path);
    dir_list = dir_list(~ismember({dir_list.name},{'.','..'}));  %除去本级和上级目录
    last_dir = dir_list(size(dir_list,1)).name;
    train_path = fullfile(train_path,last_dir,'\');
    res0=1;
    res1=1;
    switch behaviour_type
        case 'click'
            res1 = traintxt(train_path, save_path);
        case 'slide'
            res0 = trainwav(train_path, save_path);
            res1 = traintxt(train_path, save_path);
        case 'pinch'
            res1 = traintxt(train_path, save_path);
        case 'handwriting'
            res0 = trainwav(train_path, save_path);
            res1 = traintxt(train_path, save_path);
    end
    if res0 && res1 
        success = 1;
    else
        success = 0;
    end
end




