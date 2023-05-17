function [ lpc_coeff ] = my_lpc(signal, fs)

m = size(signal,1);
% Delete one of the stereo channel and transpose.
mono_signal = signal(:, 1);

% Process signal into frames.
% 每一行是一帧
framed_signal = frames(mono_signal, m, 0, 0);

% Compute LPC coefficients.
prediction_order = 12;
lpc_coeff = [];
for column_number = 1:1:size(framed_signal, 2)
    lpc_coeff = [lpc_coeff lpc(framed_signal(:, column_number), prediction_order)];
end


