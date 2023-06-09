 addpath(genpath('.\RASTA-PLP'),genpath('.\MFCC'),genpath('.\LPCC'), ...
        genpath('.\Level1'),genpath('.\LPC'),genpath('.\LSF'), ...
        genpath('.\SpectralStatistics'),genpath('.\WaveletCoef'),genpath('.\FilterBank'), ...
        genpath('.\Denoise'));

% traintxt('as/lxq/slide/','models/lxq/slide/');
% traintxt('as/xyz/slide/','models/xyz/slide/');
% traintxt('as/zjr/slide/','models/zjr/slide/');


load('models/lxq/slide/model_1.mat');
% load('models/xyz/slide/model_1.mat');
% load('models/zjr/slide/model_1.mat');

features_lxq = csvread('lxq.txt');
features_xyz = csvread('xyz.txt');
features_zjr = csvread('zjr.txt');

% 交叉验证
CVSVM = crossval(svm_model_1);
error = kfoldLoss(CVSVM);
[labels, scores] = kfoldPredict(CVSVM);
disp(mean(scores));
disp(mean(scores<0));

% 攻击验证
% [labels,scores] = predict(svm_model_1,features_zjr);
% disp(mean(scores));
% disp(mean(scores<0));

% % 交叉验证选取最好的参数
% total_score = zeros(1,100);
% total_error = zeros(1,100);
% for i= 1:2:100
%     traintxt('as/zjr/slide/','models/zjr/slide/',i);
%     load('models/zjr/slide/model_1.mat');
%     CVSVM = crossval(svm_model_1);
%     error = kfoldLoss(CVSVM);
%     [labels, scores] = kfoldPredict(CVSVM);
%     total_score(i) = mean(scores);
%     total_error(i) = error; 
% end


