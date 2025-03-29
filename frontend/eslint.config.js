import js from '@eslint/js';
import globals from 'globals';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import tseslint from 'typescript-eslint';
import stylisticConfig from "./eslint-stylistic.config.js";
import react from 'eslint-plugin-react';

export default tseslint.config(
  {
    ignores: ['dist', '**/api/**', '**/*.generated.*']
  },
  {
    extends: [js.configs.recommended, ...tseslint.configs.recommended],
    files: ['**/*.{ts,tsx}'],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
      parser: tseslint.parser,
      parserOptions: {
        project: './tsconfig.app.json',
        tsconfigRootDir: import.meta.dirname,
        sourceType: 'module',
      },
    },
    plugins: {
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
      'react': react,
      ...stylisticConfig.plugins
    },
    settings: {
      react: {
        version: 'detect',
      },
    },
    rules: {
      ...reactHooks.configs.recommended.rules,
      ...stylisticConfig.rules,

      "default-case": ["error", { "commentPattern": "^skip\\sdefault" }],
      "complexity": ["warn", 3],
      "eqeqeq": "warn",
      "max-classes-per-file": ["error", { "ignoreExpressions": true, "max": 2 }],
      "default-case-last": "error",
      "camelcase": "error",
      "capitalized-comments": "warn",
      "consistent-return": [
        "error",
        {
          "treatUndefinedAsUnspecified": true,
        }
      ],
      "no-duplicate-imports": "error",
      "no-fallthrough": "error",
      "no-param-reassign": "error",
      "no-throw-literal": "error",
      "no-unused-expressions": "error",
      "no-var": "error",
      "prefer-const": "error",
      "no-console": "error",
      "no-useless-catch": "error",
      "no-magic-numbers": ["warn", { "ignore": [1, 0] }],
      "operator-assignment": ["warn", "always"],
      "yoda": "error",
      "require-await": "error",
      "sort-imports": ["error", { ignoreCase: true, ignoreDeclarationSort: true }],


      // TypeScript rules
      "@typescript-eslint/array-type": ["error", { "default": "array-simple" }],
      "@typescript-eslint/dot-notation": "error",
      "@typescript-eslint/no-empty-function": ["error", { "allow": ["arrowFunctions"] }],
      "@typescript-eslint/no-explicit-any": "error",
      "@typescript-eslint/no-inferrable-types": ["error", { "ignoreParameters": true }],
      "@typescript-eslint/no-misused-new": "error",
      "@typescript-eslint/no-namespace": "error",
      "@typescript-eslint/no-non-null-assertion": "off",
      "@typescript-eslint/no-unused-vars": ["error", { "args": "none", "ignoreRestSiblings": true }],
      "@typescript-eslint/no-shadow": ["error", { "builtinGlobals": false, "hoist": "all", "ignoreTypeValueShadow": false }],
      "@typescript-eslint/explicit-module-boundary-types": ["error", { "allowTypedFunctionExpressions": true }],
      "@typescript-eslint/no-empty-object-type": [
        'error',
        {
          "allowInterfaces": "with-single-extends",
          "allowWithName": 'Props$'
        }
      ],

      // React specific rules
      "react/function-component-definition": [
        "error", {
        "namedComponents": "arrow-function",
        "unnamedComponents": "arrow-function"
      }],
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],
    },
  },
)
