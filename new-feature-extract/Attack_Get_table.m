addpath(genpath('.\RASTA-PLP'),genpath('.\MFCC'),genpath('.\LPCC'),genpath('.\Level1'),genpath('.\LPC'),genpath('.\LSF'),genpath('.\SpectralStatistics'),genpath('.\WaveletCoef'),genpath('.\FilterBank'));

clc;
clear;
close all;

mobile_name = 'attack-zyt';
attack_name = 'ykh';

% 设置要读取音频文件的路径
train_path = ['G:\2021-11-01-data\data_segment\infocom\data\' mobile_name '\data-seg\' attack_name '\'];
train_names = dir([train_path '*.wav']);

% 得到用于预测的数据的Table
train_Ones = {};
train_Ones = Feature_extra(train_names(1:25), train_Ones, train_path, attack_name);
T2 = cell2table(train_Ones);
filename = ['G:\2021-11-01-data\data_segment\infocom\data\' mobile_name '\table\' attack_name];
save(filename,'T2');    