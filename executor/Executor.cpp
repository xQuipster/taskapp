#include <windows.h>
#include <iostream>
#include <string>

std::wstring GetExecutablePath() {
    TCHAR buffer[MAX_PATH];
    GetModuleFileName(NULL, buffer, MAX_PATH);
    std::wstring ws(buffer);
    std::string str(ws.begin(), ws.end());
    std::string::size_type pos = str.find_last_of("\\/");
    return ws.substr(0, pos);
}

int main() {
    std::wstring exePath = GetExecutablePath();
    std::wstring javaPath = exePath + L"\\java\\bin\\java.exe";
    std::wstring jarPath = exePath + L"\\gui.jar";

    std::wstring command = L"\"" + javaPath + L"\" -jar \"" + jarPath + L"\"";

    PROCESS_INFORMATION processInfo;
    STARTUPINFO startupInfo;
    ZeroMemory(&startupInfo, sizeof(startupInfo));
    startupInfo.cb = sizeof(startupInfo);
    startupInfo.dwFlags |= STARTF_USESHOWWINDOW;
    startupInfo.wShowWindow = SW_HIDE;

    if (!CreateProcess(NULL,
        const_cast<LPWSTR>(command.c_str()),
        NULL, NULL, FALSE, CREATE_NO_WINDOW, NULL, NULL, &startupInfo, &processInfo)) {
        return 1;
    }

    CloseHandle(processInfo.hProcess);
    CloseHandle(processInfo.hThread);

    return 0;
}
