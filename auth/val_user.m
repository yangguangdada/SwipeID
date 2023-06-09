function result = val_user(username,behaviour_type, val_path, model_path)
    % positive_name 正类名称
    % model_path svm模型home路径
    % val_path 测试数据集路径  data/val
    % scores 每个测试样本的分数
    % result 1 代表合法用户 0代表非法用户 
    model_path = fullfile(model_path,username, behaviour_type,'\');
    model_name_0 = 'model_0.mat';
    model_name_1 = 'model_1.mat';
    model_path_0 = fullfile(model_path,model_name_0);
    model_path_1 = fullfile(model_path,model_name_1);
    % 加载模型 模型变量名svm_model
    % 查找最新的验证集
    val_path = fullfile(val_path, username, behaviour_type, '\');
    dir_list = dir(val_path);
    dir_list = dir_list(~ismember({dir_list.name},{'.','..'}));  %除去本级和上级目录
    last_dir = dir_list(size(dir_list,1)).name;
    val_path = fullfile(val_path,last_dir,'\');
    scores_0 = [];
    scores_1 = [];
    switch behaviour_type
        case 'click'
            scores_1 = valtxt(val_path, model_path_1);
        case 'slide'
            scores_0 = valwav(val_path, model_path_0);
            scores_1 = valtxt(val_path, model_path_1);
        case 'pinch'
            scores_1 = valtxt(val_path, model_path_1);
        case 'handwriting'
            scores_0 = valwav(val_path, model_path_0);
            scores_1 = valtxt(val_path, model_path_1);
    end
    threshold_wav = -0.1;
    threshold_txt = -0.05;
    auth0 = 1;
    auth1 = 1; 
    if(size(scores_0, 1) ~= 0) %双因素
        if(mean(scores_0)<threshold_wav) 
            auth0=0;
        end
        if(mean(scores_1)<threshold_txt) 
            auth1=0;
        end
    else                       %单因素
        if(mean(scores_1)<threshold_txt) 
            auth1=0;
        end
    end
    if(auth0 && auth1)
        result = 1;
    else 
        result = 0;
    end
end

