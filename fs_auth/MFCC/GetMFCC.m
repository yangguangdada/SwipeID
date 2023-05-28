function [ MFCC_features ] = GetMFCC( audioIn, fs )
m = size(audioIn,1);
MFCCaFE = audioFeatureExtractor( ...
    "SampleRate",fs, ...
    "Window",hamming(m,"periodic"), ...  % 每帧m个采样点
    "OverlapLength",(0), ...  %  帧与帧之间有0个采样点重合
    "mfcc",true, ...  % MFCC
    "mfccDelta",true, ...  % MFC-delta
    "mfccDeltaDelta",true, ...  % MFC-delta-delta
    "spectralCentroid",false);

%mfc：十二个mfcc系数和一个能量 以及一阶和二阶差分 共36个参数 每一行为一帧数据
MFCC_features = extract(MFCCaFE,audioIn);

end
