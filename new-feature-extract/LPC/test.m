
% Declare variables.
filename = "D:/code/2021-11-01-data/after_one/Denoise/12-6pixel4/ysy1-Denoise.wav";
[signal, fs] = audioread(filename);
lpc_feature = my_lpc(signal,fs);