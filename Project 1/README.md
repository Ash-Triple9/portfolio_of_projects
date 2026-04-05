# Energy Prosumer Behavior Forecasting

## Overview
A machine learning project addressing the lack of reliable energy prediction among prosumers — individuals and entities that both consume and produce energy (e.g., homes with solar panels). Built an end-to-end Python ML pipeline to forecast prosumer energy behavior.

## Problem
Prosumers create unpredictable fluctuations in energy grids. Accurate forecasting of their behavior is critical for grid stability and energy planning, yet reliable models are scarce.

## Tools & Technologies
- **Language:** Python
- **Libraries:** Pandas, NumPy, scikit-learn, Matplotlib, Seaborn
- **Environment:** Google Colab

## Pipeline
1. **Data Loading & Cleaning** — handled missing values, outliers, and inconsistent records
2. **EDA** — visualized consumption/production patterns across time and user segments
3. **Feature Engineering** — derived time-based, statistical, and interaction features
4. **Model Selection** — evaluated multiple regression models against a baseline
5. **Evaluation** — measured RMSE, MAE, and R² across models

## Results
Final model outperformed the baseline regression model by **40%**, demonstrating the value of thoughtful feature engineering and model selection over naive approaches.
