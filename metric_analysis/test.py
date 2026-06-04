import matplotlib.pyplot as plt
import numpy as np


print("Hello World")

for i in range(5):
    print(i)


plt.style.use('_mpl-gallery-nogrid')

# make data: correlated + noise
np.random.seed(1)
x = np.random.randn(50000)
y = 1.2 * x + np.random.randn(50000) / 3

z = np.ones(50000) * -x

# plot:
fig, ax = plt.subplots()

ax.hist2d(x, z, bins=(np.arange(-3, 3, 0.1), np.arange(-3, 3, 0.1)))

ax.set(xlim=(-2, 2), ylim=(-3, 3))

plt.show()