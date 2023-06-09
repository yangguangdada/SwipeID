
import csv
import numpy as np

# 读取数据文件
with open('lxq.txt', 'r') as f:
    reader = csv.reader(f)
    data = []
    for row in reader:
        # 将每行数据转换为浮点数
        row = [float(x) for x in row]
        data.append(row)

# 将数据转换为NumPy数组
data = np.array(data)

def jitter(features, scale=0.2):
    # 将 features 转换为二维数组
    features_2d = features.reshape((features.shape[0], -1))
    # 生成随机扰动向量
    jitter = np.random.normal(loc=0, scale=scale, size=features_2d.shape)
    # 对特征向量进行扰动
    features_jittered_2d = features_2d + jitter
    # 将特征向量转换为原来的形状
    features_jittered = features_jittered_2d.reshape(features.shape)
    return features_jittered

# 对数据集进行抖动增强
n_jitters = 5 # 每个数据点增强的数量
jittered_data = []
for i in range(data.shape[0]):
    features = data[i, :-1]
    # label = data[i, -1]
    # 对每个数据点进行抖动增强
    for j in range(n_jitters):
        jittered_features = jitter(features)
        # 将增强后的数据点和标签添加到列表中
        # jittered_data.append(list(jittered_features) + [label])
        jittered_data.append(list(jittered_features))
# 将增强后的数据保存为txt文件
with open('lxq_augment.txt', 'w', newline='') as f:
    writer = csv.writer(f)
    writer.writerows(jittered_data)