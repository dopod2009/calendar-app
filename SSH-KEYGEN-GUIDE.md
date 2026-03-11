# SSH密钥生成详细步骤

## 🚀 执行命令

在终端中粘贴并执行：

```bash
ssh-keygen -t ed25519 -C "hdmforlove@gmail.com"
```

---

## 📝 交互式操作指引

### 第1步：选择保存路径

终端显示：
```
Generating public/private ed25519 key pair.
Enter file in which to save the key (/Users/alex/.ssh/id_ed25519): 
```

**操作**: 直接按 **Enter** 键（使用默认路径）

---

### 第2步：设置密码（可选）

终端显示：
```
Enter passphrase (empty for no passphrase): 
```

**操作**: 直接按 **Enter** 键（不设置密码，使用更方便）

**说明**: 如果设置密码，每次使用密钥都需要输入密码。建议不设置。

---

### 第3步：确认密码

终端显示：
```
Enter same passphrase again: 
```

**操作**: 再次按 **Enter** 键

---

## ✅ 成功标志

看到以下输出表示密钥生成成功：

```
Your identification has been saved in /Users/alex/.ssh/id_ed25519
Your public key has been saved in /Users/alex/.ssh/id_ed25519.pub
The key fingerprint is:
SHA256:xxxxxxxxxxxxxxxxxxxxxxxxxxxxx hdmforlove@gmail.com
The key's randomart image is:
+--[ED25519 256]--+
|        ...      |
|       ...       |
+----[SHA256]-----+
```

---

## ⏱️ 预计时间

约 **10秒**

---

## 🎯 完成后的下一步

密钥生成成功后，需要：

### 步骤3: 查看并复制公钥

执行：
```bash
cat ~/.ssh/id_ed25519.pub
```

复制输出的整行内容（以 `ssh-ed25519` 开头）

---

### 步骤4: 添加到GitHub

访问：https://github.com/settings/ssh/new

粘贴公钥内容，保存。

---

### 步骤5: 测试连接

执行：
```bash
ssh -T git@github.com
```

看到 `Hi dopod2009!` 表示成功。

---

## 📋 完整流程总结

```
1. 执行命令：ssh-keygen -t ed25519 -C "hdmforlove@gmail.com"
2. 按3次Enter（使用默认设置）
3. 查看公钥：cat ~/.ssh/id_ed25519.pub
4. 复制公钥内容
5. 添加到GitHub：https://github.com/settings/ssh/new
6. 测试连接：ssh -T git@github.com
7. 推送代码：git push -u origin main
```

---

**现在请在终端执行命令，并按3次Enter键完成密钥生成！**
