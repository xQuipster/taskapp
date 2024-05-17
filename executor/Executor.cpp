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
    // Отримання шляху до виконуваного файлу .exe
    std::wstring exePath = GetExecutablePath();

    // Шлях до Java
    std::wstring javaPath = exePath + L"\\java\\bin\\java.exe";
    // Шлях до вашого .jar файлу (припустимо, що він знаходиться в одній папці з .exe файлом)
    std::wstring jarPath = exePath + L"\\gui.jar";

    // Створення команди для виклику Java з вашим .jar файлом
    std::wstring command = L"\"" + javaPath + L"\" -jar \"" + jarPath + L"\"";

    // Створення об'єкту PROCESS_INFORMATION
    PROCESS_INFORMATION processInfo;
    // Створення об'єкту STARTUPINFO
    STARTUPINFO startupInfo;
    ZeroMemory(&startupInfo, sizeof(startupInfo));
    startupInfo.cb = sizeof(startupInfo);
    startupInfo.dwFlags |= STARTF_USESHOWWINDOW; // Використовувати параметр wShowWindow
    startupInfo.wShowWindow = SW_HIDE; // Приховати вікно

    // Створення нового процесу
    if (!CreateProcess(NULL,   // Назва модуля, щоб викликати. NULL, оскільки використовується команда shell
        const_cast<LPWSTR>(command.c_str()), // Команда для виконання
        NULL, NULL, FALSE, CREATE_NO_WINDOW, NULL, NULL, &startupInfo, &processInfo)) {
        // В разі помилки
        printf("Помилка створення процесу: %d\n", GetLastError());
        return 1;
    }

    // Закриття дескрипторів, які вже не потрібні
    CloseHandle(processInfo.hProcess);
    CloseHandle(processInfo.hThread);

    return 0;
}
