
 addpath(genpath('.\RASTA-PLP'),genpath('.\MFCC'),genpath('.\LPCC'), ...
        genpath('.\Level1'),genpath('.\LPC'),genpath('.\LSF'), ...
        genpath('.\SpectralStatistics'),genpath('.\WaveletCoef'),genpath('.\FilterBank'), ...
        genpath('.\Denoise'),genpath('.\utils'));
    
% register_user('lxq', 'slide','as','models');
% register_user('zjr', 'slide','as','models');

val_user('lxq','handwriting','../data/val','../Models');
% val_user('zjr','slide','val','models');

% valtxt('../data/val/lxq/click/1686239708835/', '../models/lxq/click/model_1.mat');