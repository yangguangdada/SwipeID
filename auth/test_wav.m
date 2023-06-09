
% trainwav('as/lxq/slide/','models/lxq/slide/');
% trainwav('as/zjr/slide/','models/zjr/slide/');

% 特征加载脚本
load('models/lxq/slide/model_0.mat');
features_lxq = features_wav;
load('models/zjr/slide/model_0.mat');
features_zjr = features_wav;

% 模型加载
load('models/lxq/slide/model_0.mat');
% load('models/zjr/slide/model_0.mat');

% 攻击验证
[labels,scores] = predict(svm_model_0,features_zjr);
disp(mean(scores));
disp(mean(scores<0));

% 交叉验证
% CVSVM = crossval(svm_model_0);
% error = kfoldLoss(CVSVM);
% [labels, scores] = kfoldPredict(CVSVM);
% disp(mean(scores));
% disp(mean(scores<0));



% features_zjr  ={};
% names = dir('as/zjr/slide/denoise/*.wav');
% for idx = 1:numel(train_de_aug_wavs)
%         feature_wav = Feature_extra(train_de_aug_wavs(idx).name, den_dirpath); 
%         features_wav(idx,:) = feature_wav; 
% end















% % 交叉验证选取最好的参数
% den_dirpath = 'as/lxq/slide/denoise';
% train_de_aug_wavs = dir(['as/lxq/slide/denoise/' '*.wav']);
% features_wav = zeros(size(train_de_aug_wavs,1),99); % 预先分配对象
% total_score=[];
% c = 1;
% for i = 0.01:0.03:0.5
%     for idx = 1:numel(train_de_aug_wavs)
%         feature_wav = Feature_extra(train_de_aug_wavs(idx).name, den_dirpath); 
%         features_wav(idx,:) = feature_wav; 
%     end
%     Y = ones(1,size(features_wav,1));
%     % 训练svm
%     svm_model_0 = fitcsvm(features_wav, Y, 'KernelFunction','gaussian', 'KernelScale', 'auto', ...
%                 'Standardize', true,'OutlierFraction', 0.04, 'Nu', i);
%     CVSVM = crossval(svm_model_0);
%     error = kfoldLoss(CVSVM);
%     [labels, scores] = kfoldPredict(CVSVM);
%     total_score(c) = mean(scores);
%     c = c+1;
% end






