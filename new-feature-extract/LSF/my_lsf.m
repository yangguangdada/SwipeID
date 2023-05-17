function [ lsf_coeff ] = my_lpc(signal, fs)

m = size(signal,1);
% Delete one of the stereo channel and transpose.
mono_signal = signal(:, 1);

% Process signal into frames.
% 每一行是一帧
framed_signal = frames(mono_signal, m, 0, 0);

% Compute LPC coefficients and convert to LSF
prediction_order = 12;
lpc_coeff = [];
lsf_coeff = [];
for column_number = 1:1:size(framed_signal, 2)
    tmp = lpc(framed_signal(:, column_number), prediction_order);
    % Convert LPC to LSF
    lsf_coeff = [lsf_coeff poly2lsf(tmp)];
end

lsf_coeff = lsf_coeff';



