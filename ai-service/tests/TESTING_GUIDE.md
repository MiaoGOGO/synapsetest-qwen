# 🎯 Cursor 测试功能指南

本文档详细说明如何在 Cursor 中配置、运行和调试 Python 测试。

## 🚀 快速开始：启用测试按钮

如果你看不到测试方法上方的 `Run Test` / `Debug Test` 按钮，请按以下步骤操作：

### 1. 检查环境配置
我们已经自动完成了以下配置：
- ✅ 安装了 `pytest` 及相关依赖
- ✅ 更新了 `.vscode/settings.json` 启用测试发现
- ✅ 创建了 `pytest.ini` 和 `conftest.py`
- ✅ 更新了 `.vscode/launch.json` 调试配置

### 2. 彻底重启 Cursor
**重要：** 仅重新加载窗口可能不够。
1. 按 `Cmd+Q` (Mac) 或 `Alt+F4` (Windows) **完全退出** Cursor。
2. 重新打开 Cursor。

### 3. 选择 Python 解释器
1. 按 `Cmd+Shift+P` (Mac) 或 `Ctrl+Shift+P` (Windows) 打开命令面板。
2. 输入并选择：`Python: Select Interpreter`。
3. 选择包含 pytest 的环境（推荐：`/Users/weimeng/miniconda3/bin/python`）。
   - 如果没看到，选择 "Enter interpreter path..." 并输入上述路径。

### 4. 手动触发测试发现
1. 打开命令面板 (`Cmd+Shift+P`)。
2. 输入并选择：`Python: Discover Tests`。
3. 等待几秒钟，查看左侧活动栏是否出现 **测试图标**（烧瓶/试管 🧪）。

---

## 🛠 使用指南

### 1. 使用代码内按钮（推荐）
在 `ai-service/tests/test_testcase_generation.py` 等测试文件中，每个测试类和方法上方会自动出现：
- **▶️ Run Test**: 运行单个测试
- **🐛 Debug Test**: 调试单个测试（支持断点）

### 2. 使用测试侧边栏
1. 点击左侧活动栏的 **测试图标** 🧪。
2. 展开测试树查看所有测试用例。
3. 右键点击任意文件、类或方法，选择 Run 或 Debug。

### 3. 使用快捷键
- `Ctrl+; Ctrl+A`: 运行所有测试
- `Ctrl+; Ctrl+F`: 运行当前文件的测试
- `Ctrl+; Ctrl+L`: 运行上次失败的测试

### 4. 调试模式 (F5)
如果代码内按钮不可用，可以使用标准调试流程：
1. 在测试代码中设置断点（点击行号左侧）。
2. 打开要调试的测试文件。
3. 按 `F5` 启动调试。
4. 选择调试配置：
   - `Debug pytest: Current Test` (调试当前选中的测试)
   - `Debug pytest: test_testcase_generation` (调试特定文件)

---

## 💻 命令行运行测试

如果你更喜欢终端，可以使用以下命令：

```bash
cd ai-service

# 运行所有测试
pytest tests/

# 运行特定文件
pytest tests/test_testcase_generation.py -v

# 运行特定测试方法
pytest tests/test_testcase_generation.py::TestAITestCaseGenerationAPI::test_scenario_1_generate_cases_with_valid_input -v

# 运行并显示输出 (stdout)
pytest tests/test_testcase_generation.py -v -s

# 生成覆盖率报告
pytest tests/ --cov=. --cov-report=html
```

---

## ❓ 故障排查

如果测试按钮仍然没有出现，请按以下顺序排查：

### 方法 A：检查输出日志
1. 打开命令面板 (`Cmd+Shift+P`)。
2. 输入 `Output: Show Output`。
3. 在下拉菜单中选择 **Python Test Log**。
4. 查看是否有导入错误或配置错误。

### 方法 B：清除缓存重试
在终端运行以下命令，清除 pytest 缓存：
```bash
cd ai-service
rm -rf .pytest_cache __pycache__ tests/__pycache__
```
然后重复"手动触发测试发现"步骤。

### 方法 C：验证 pytest 安装
在终端运行手动测试，确认环境正常：
```bash
cd ai-service
python -m pytest tests/test_simple.py -v
```
如果此命令成功（显示测试通过），说明环境配置正确，问题在于 Cursor/VS Code 插件识别。此时建议使用 **测试侧边栏** 或 **F5 调试** 作为替代。

### 方法 D：检查 Python 扩展
1. 打开扩展面板 (`Cmd+Shift+X`)。
2. 搜索 "Python"。
3. 确保扩展已安装且启用。尝试禁用后重新启用。

---

## 📋 检查清单

- [ ] Python 解释器路径正确 (`/Users/weimeng/miniconda3/bin/python`)
- [ ] 状态栏显示正确的 Python 版本
- [ ] `pytest.ini` 位于 `ai-service` 根目录
- [ ] 左侧测试视图（🧪）能看到测试树
- [ ] 能够通过命令行成功运行 `pytest`

如有其他问题，请查看 `ai-service/tests/conftest.py` 确保路径配置正确。

