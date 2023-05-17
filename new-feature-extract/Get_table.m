addpath(genpath('.\RASTA-PLP'),genpath('.\MFCC'),genpath('.\LPCC'),genpath('.\Level1'),genpath('.\LPC'),genpath('.\LSF'),genpath('.\SpectralStatistics'),genpath('.\WaveletCoef'),genpath('.\FilterBank'));

clc;
clear;
close all;

mobile_name = 'yjh';

% 设置要读取音频文件的路径
train_path = ['E:\IJIDEA\code\data\' mobile_name '\train\'];
train_names = dir([train_path '*.wav']);
train_Ones = {};

% 得到用于训练的数据的Table
train_Ones = Feature_extra(train_names, train_Ones, train_path, mobile_name);
T1 = cell2table(train_Ones);
filename = ['E:\IJIDEA\code\data\' mobile_name '\table\train'];
save(filename,'T1');

% 设置要读取音频文件的路径
train_path = ['E:\IJIDEA\code\data\' mobile_name '\test-day9\'];
train_names = dir([train_path '*.wav']);
train_Ones = {};

% 得到用于测试的数据的Table
tic
train_Ones = {};
train_Ones = Feature_extra(train_names, train_Ones, train_path, mobile_name);
T2 = cell2table(train_Ones);
toc
filename = ['E:\IJIDEA\code\data\' mobile_name '\table\test'];
save(filename,'T2');    

% % 得到用于预测的数据的Table
% train_Ones = {};
% train_Ones = Feature_extra(train_names(141:175), train_Ones, train_path, mobile_name);
% T3 = cell2table(train_Ones);
% filename = ['C:\2021-11-01-data\data_segment\infocom\data\' mobile_name '\table\try'];
% save(filename,'T3');    