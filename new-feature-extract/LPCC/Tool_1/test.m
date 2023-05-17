
% Learning models. Returns containers.Map
models = learn_models('models');

% Declare variables.
filename = "wav_Denoise/zyt-bot-Denoise.wav";
[signal, fs] = audioread(filename);
ainfo = audioinfo(filename);
x = ainfo.BitsPerSample;

lpcc_feature = my_lpcc(signal);