
% Learning models. Returns containers.Map
models = learn_models('models');

% Declare variables.
filename = "wav_Denoise/ysy-bot-Denoise.wav";
[signal, fs] = audioread(filename);
lpcc_feature = my_lpcc(signal);