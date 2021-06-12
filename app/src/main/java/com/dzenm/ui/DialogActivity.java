package com.dzenm.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.dialog.MaterialDialog;
import com.dzenm.dialog.PromptDialog;
import com.dzenm.dialog.Utils;

public class DialogActivity extends AppCompatActivity {

    private final String[] mItems = new String[]{"Item 1", "Item 2", "Item 3", "Item 4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        setTheme(R.style.AppTheme_Light);

        pressed(findViewById(R.id.tv_11), android.R.color.holo_red_dark);
        ripple(findViewById(R.id.tv_12), android.R.color.holo_green_dark);
        pressed(findViewById(R.id.tv_13), android.R.color.holo_blue_bright);
        ripple(findViewById(R.id.tv_14), android.R.color.holo_red_light);
        pressed(findViewById(R.id.tv_15), android.R.color.holo_orange_dark);
        ripple(findViewById(R.id.tv_16), android.R.color.holo_green_dark);

        ripple(findViewById(R.id.tv_211), android.R.color.holo_green_light);
        pressed(findViewById(R.id.tv_221), android.R.color.holo_blue_bright);
        ripple(findViewById(R.id.tv_231), android.R.color.holo_orange_light);
        pressed(findViewById(R.id.tv_241), android.R.color.holo_red_dark);

        ripple(findViewById(R.id.tv_21), android.R.color.holo_blue_dark);
        pressed(findViewById(R.id.tv_22), android.R.color.holo_orange_dark);
        ripple(findViewById(R.id.tv_23), android.R.color.holo_red_dark);
        pressed(findViewById(R.id.tv_24), android.R.color.holo_green_dark);
        ripple(findViewById(R.id.tv_25), android.R.color.holo_red_light);
        pressed(findViewById(R.id.tv_26), android.R.color.holo_green_light);

        ripple(findViewById(R.id.tv_40), android.R.color.holo_orange_light);
        pressed(findViewById(R.id.tv_41), android.R.color.holo_red_light);
        ripple(findViewById(R.id.tv_42), android.R.color.holo_blue_dark);

        pressed(findViewById(R.id.tv_43), android.R.color.holo_orange_dark);
        ripple(findViewById(R.id.tv_44), android.R.color.holo_red_dark);
        pressed(findViewById(R.id.tv_45), android.R.color.holo_blue_dark);

        ripple(findViewById(R.id.tv_loading_success), android.R.color.holo_blue_dark);
        pressed(findViewById(R.id.tv_loading_error), android.R.color.holo_red_dark);
        ripple(findViewById(R.id.tv_loading_warming), android.R.color.holo_green_dark);
        pressed(findViewById(R.id.tv_loading_success_return), android.R.color.holo_orange_dark);
        ripple(findViewById(R.id.tv_loading_custom), android.R.color.holo_blue_light);
        pressed(findViewById(R.id.tv_loading_1), android.R.color.holo_red_light);
        ripple(findViewById(R.id.tv_loading_2), android.R.color.holo_green_light);
        pressed(findViewById(R.id.tv_loading_3), android.R.color.holo_orange_light);
        ripple(findViewById(R.id.tv_loading_4), android.R.color.darker_gray);
        pressed(findViewById(R.id.tv_loading_5), android.R.color.holo_blue_bright);
    }

    public static void pressed(View viewBackground, int color) {
        int c = Utils.getColor(viewBackground.getContext(), R.attr.dialogPrimaryColor);
        viewBackground.setBackground(Utils.pressed(viewBackground.getContext(), color, c, 20f));
    }

    public static void ripple(View viewBackground, int color) {
        int c = Utils.getColor(viewBackground.getContext(), R.attr.inactiveColor);
        viewBackground.setBackground(Utils.ripple(viewBackground.getContext(), color, c, 20f));
    }

    public void onClick(View view) {
        if (view.getId() == R.id.tv_11) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setNeutralClickListener("CANCEL", new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveClickListener("ACCEPT", new MaterialDialog.OnClickListener() {
                @Override
                public void onClick(MaterialDialog dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else if (view.getId() == R.id.tv_12) {
            new MaterialDialog.Builder(this)
                    .setMessage("Message")
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_13) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setMessage("Message")
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_14) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setMessage("Message")
                    .setIcon(R.drawable.ic_warm)
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_15) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setMessage("Message")
                    .setButtonText("ACCECP", "DECLINE", "NEUTRAL")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

        } else if (view.getId() == R.id.tv_16) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setMessage("Message")
                    .setButtonText("ACCECP", "DECLINE", "NEUTRAL")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(final MaterialDialog materialDialog, int which) {
                            if (which == MaterialDialog.BUTTON_POSITIVE) {
                                new MaterialDialog.Builder(DialogActivity.this)
                                        .setTitle("Title")
                                        .setMessage("Message")
                                        .setButtonText("ACCECP", "DECLINE")
                                        .setDimAccount(0.8f)
                                        .setOnClickListener(new MaterialDialog.OnClickListener() {
                                            @Override
                                            public void onClick(MaterialDialog dialog, int which) {
                                                dialog.dismiss();
                                                materialDialog.dismiss();
                                            }
                                        }).create().show();
                            } else {
                                new MaterialDialog.Builder(DialogActivity.this)
                                        .setTitle("Title")
                                        .setMessage("Message")
                                        .setButtonText("ACCECP", "DECLINE")
                                        .setDimAccount(0.8f)
                                        .setOnClickListener(new MaterialDialog.OnClickListener() {
                                            @Override
                                            public void onClick(MaterialDialog dialog, int which) {
                                                dialog.dismiss();
                                                materialDialog.dismiss();
                                            }
                                        }).create().show();
                            }
                        }
                    }).create().show();

        } else if (view.getId() == R.id.tv_211) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setMaterialDesign(false)
                    .setButtonText("ACCECP")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_221) {
            new MaterialDialog.Builder(this)
                    .setMessage("Message")
                    .setMaterialDesign(false)
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_231) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setMessage("Message")
                    .setMaterialDesign(false)
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_241) {
            new MaterialDialog.Builder(this)
                    .setIcon(R.drawable.ic_warm)
                    .setMessage("Message")
                    .setMaterialDesign(false)
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_21) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setMessage("Hello Message")
                    .setItem(mItems)
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnItemClickListener(new MaterialDialog.OnItemClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            Toast.makeText(DialogActivity.this, "" + mItems[which], Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_22) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setItem(mItems)
                    .setNeutralClickListener("CANCEL", new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveClickListener("ACCEPT", new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setOnSingleClickListener(new MaterialDialog.OnSingleClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which, boolean isChecked) {
                            Toast.makeText(DialogActivity.this, "" + mItems[which] + ", checked: " + isChecked, Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_23) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setItem(mItems)
                    .setNeutralClickListener("CANCEL", new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveClickListener("ACCEPT", new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setOnMultipleClickListener(new MaterialDialog.OnMultipleClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which, boolean isChecked) {
                            Toast.makeText(DialogActivity.this, "" + mItems[which] + ", checked: " + isChecked, Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_24) {
            new MaterialDialog.Builder(this)
                    .setItem(mItems)
                    .setMaterialDesign(false)
                    .setOnItemClickListener(new MaterialDialog.OnItemClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            Toast.makeText(DialogActivity.this, "" + mItems[which], Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_25) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setItem(mItems)
                    .setMaterialDesign(false)
                    .setOnSingleClickListener(new MaterialDialog.OnSingleClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which, boolean isChecked) {
                            Toast.makeText(DialogActivity.this, "" + mItems[which] + ", checked: " + isChecked, Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_26) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setItem(mItems)
                    .setMaterialDesign(false)
                    .setOnMultipleClickListener(new MaterialDialog.OnMultipleClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which, boolean isChecked) {
                            Toast.makeText(DialogActivity.this, "" + mItems[which] + ", checked: " + isChecked, Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_40) {
            String message = "从方法的名称中可以看出该方法主要是负责分发，是安卓事件分发过程中的核心。事件是如何传递的，主要就是看该方法，理解了这个方法，也就理解了安卓事件分发机制。\n" +
                    "\n" +
                    "在了解该方法的核心机制之前，需要知道一个结论：\n" +
                    "\n" +
                    "如果某个组件的该方法返回TRUE,则表示该组件已经对事件进行了处理，不用继续调用其余组件的分发方法，即停止分发。\n" +
                    "如果某个组件的该方法返回FALSE,则表示该组件不能对该事件进行处理，需要按照规则继续分发事件。在不复写该方法的情况下，除了一些特殊的组件，其余组件都是默认返回False的。后续有例子说明。\n" +
                    "为何返回TRUE就不用继续分发，而返回FALSE就停止分发呢？为了解决这个疑问，需要看一看该方法的具体分发逻辑。为了便于理解，下面对dispatchTouchEvent方法进行简化，只保留最核心的逻辑。" +
                    "从方法的名称中可以看出该方法主要是负责分发，是安卓事件分发过程中的核心。事件是如何传递的，主要就是看该方法，理解了这个方法，也就理解了安卓事件分发机制。\n" +
                    "\n" +
                    "在了解该方法的核心机制之前，需要知道一个结论：\n" +
                    "\n" +
                    "如果某个组件的该方法返回TRUE,则表示该组件已经对事件进行了处理，不用继续调用其余组件的分发方法，即停止分发。\n" +
                    "如果某个组件的该方法返回FALSE,则表示该组件不能对该事件进行处理，需要按照规则继续分发事件。在不复写该方法的情况下，除了一些特殊的组件，其余组件都是默认返回False的。后续有例子说明。\n" +
                    "为何返回TRUE就不用继续分发，而返回FALSE就停止分发呢？为了解决这个疑问，需要看一看该方法的具体分发逻辑。为了便于理解，下面对dispatchTouchEvent方法进行简化，只保留最核心的逻辑。" +
                    "如果某个组件的该方法返回TRUE,则表示该组件已经对事件进行了处理，不用继续调用其余组件的分发方法，即停止分发。\n" +
                    "如果某个组件的该方法返回FALSE,则表示该组件不能对该事件进行处理，需要按照规则继续分发事件。在不复写该方法的情况下，除了一些特殊的组件，其余组件都是默认返回False的。后续有例子说明。\n" +
                    "为何返回TRUE就不用继续分发，而返回FALSE就停止分发呢？为了解决这个疑问，需要看一看该方法的具体分发逻辑。为了便于理解，下面对dispatchTouchEvent方法进行简化，只保留最核心的逻辑。" +
                    "如果某个组件的该方法返回TRUE,则表示该组件已经对事件进行了处理，不用继续调用其余组件的分发方法，即停止分发。\n" +
                    "如果某个组件的该方法返回FALSE,则表示该组件不能对该事件进行处理，需要按照规则继续分发事件。在不复写该方法的情况下，除了一些特殊的组件，其余组件都是默认返回False的。后续有例子说明。\n" +
                    "为何返回TRUE就不用继续分发，而返回FALSE就停止分发呢？为了解决这个疑问，需要看一看该方法的具体分发逻辑。为了便于理解，下面对dispatchTouchEvent方法进行简化，只保留最核心的逻辑。" +
                    "如果某个组件的该方法返回TRUE,则表示该组件已经对事件进行了处理，不用继续调用其余组件的分发方法，即停止分发。\n" +
                    "如果某个组件的该方法返回FALSE,则表示该组件不能对该事件进行处理，需要按照规则继续分发事件。在不复写该方法的情况下，除了一些特殊的组件，其余组件都是默认返回False的。后续有例子说明。\n" +
                    "为何返回TRUE就不用继续分发，而返回FALSE就停止分发呢？为了解决这个疑问，需要看一看该方法的具体分发逻辑。为了便于理解，下面对dispatchTouchEvent方法进行简化，只保留最核心的逻辑。" +
                    "如果某个组件的该方法返回TRUE,则表示该组件已经对事件进行了处理，不用继续调用其余组件的分发方法，即停止分发。\n" +
                    "如果某个组件的该方法返回FALSE,则表示该组件不能对该事件进行处理，需要按照规则继续分发事件。在不复写该方法的情况下，除了一些特殊的组件，其余组件都是默认返回False的。后续有例子说明。\n" +
                    "为何返回TRUE就不用继续分发，而返回FALSE就停止分发呢？为了解决这个疑问，需要看一看该方法的具体分发逻辑。为了便于理解，下面对dispatchTouchEvent方法进行简化，只保留最核心的逻辑。" +
                    "如果某个组件的该方法返回TRUE,则表示该组件已经对事件进行了处理，不用继续调用其余组件的分发方法，即停止分发。\n" +
                    "如果某个组件的该方法返回FALSE,则表示该组件不能对该事件进行处理，需要按照规则继续分发事件。在不复写该方法的情况下，除了一些特殊的组件，其余组件都是默认返回False的。后续有例子说明。\n" +
                    "为何返回TRUE就不用继续分发，而返回FALSE就停止分发呢？为了解决这个疑问，需要看一看该方法的具体分发逻辑。为了便于理解，下面对dispatchTouchEvent方法进行简化，只保留最核心的逻辑。" +
                    "如果某个组件的该方法返回TRUE,则表示该组件已经对事件进行了处理，不用继续调用其余组件的分发方法，即停止分发。\n" +
                    "如果某个组件的该方法返回FALSE,则表示该组件不能对该事件进行处理，需要按照规则继续分发事件。在不复写该方法的情况下，除了一些特殊的组件，其余组件都是默认返回False的。后续有例子说明。\n" +
                    "为何返回TRUE就不用继续分发，而返回FALSE就停止分发呢？为了解决这个疑问，需要看一看该方法的具体分发逻辑。为了便于理解，下面对dispatchTouchEvent方法进行简化，只保留最核心的逻辑。";
            new MaterialDialog.Builder(this)
                    .setMessage(message)
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_41) {
            String url = "https://downpack.baidu.com/appsearch_AndroidPhone_v8.0.3(1.0.65.172)_1012271b.apk";
//            UpGradeView upGradeView = new UpGradeView(this);
//            upGradeView.setUrl(url)
//                    .setVersionName("v1.1")
//                    .setDesc("我也不知道更新了什么")
//                    .setSize("43.8M")
//                    .setCanCancel(false);
//            new MaterialDialog.Builder(this).setView(upGradeView).create().show();
        } else if (view.getId() == R.id.tv_42) {
            String url = "https://downpack.baidu.com/appsearch_AndroidPhone_v8.0.3(1.0.65.172)_1012271b.apk";
//            UpGradeView upGradeView = new UpGradeView(this);
//            upGradeView.setUrl(url)
//                    .setVersionName("v1.1")
//                    .setDesc("我也不知道更新了什么")
//                    .setSize("43.8M")
//                    .setUpgradeImage(R.drawable.ic_upgrade_top)
//                    .setCanCancel(false);
//            new MaterialDialog.Builder(this).setView(upGradeView).create().show();
        } else if (view.getId() == R.id.tv_43) {
            new MaterialDialog.Builder(this)
                    .setItem("拍照", "图片", "取消")
                    .setOnItemClickListener(new MaterialDialog.OnItemClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            Toast.makeText(DialogActivity.this, "" + which, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_44) {
            new MaterialDialog.Builder(this)
                    .setItem("拍照", "图片", "取消")
                    .setGravity(Gravity.BOTTOM)
                    .setOnItemClickListener(new MaterialDialog.OnItemClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            Toast.makeText(DialogActivity.this, "" + which, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_45) {
            new MaterialDialog.Builder(this)
                    .setItem("拍照", "图片")
                    .setMaterialDesign(false)
                    .setOnItemClickListener(new MaterialDialog.OnItemClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            Toast.makeText(DialogActivity.this, "" + which, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_loading_success) {
            PromptDialog.newInstance(this)
                    .setTranslucent(true)
                    .showSuccess("登录成功");
        } else if (view.getId() == R.id.tv_loading_error) {
            PromptDialog.newInstance(this)
                    .setBackground(Utils.drawableOf(this, android.R.color.holo_purple, 8f))
                    .showError("禁止访问");
        } else if (view.getId() == R.id.tv_loading_warming) {
            PromptDialog.newInstance(this)
                    .setGravity(Gravity.BOTTOM)
                    .showWarming("您的身份信息可能被泄露");
        } else if (view.getId() == R.id.tv_loading_custom) {
            PromptDialog.newInstance(this)
                    .setText("正在加载中, 请稍后...")
                    .setIcon(R.drawable.ic_warm)
                    .autoDismiss();
        } else if (view.getId() == R.id.tv_loading_success_return) {
            PromptDialog.newInstance(this)
                    .setTranslucent(true)
                    .setOnCountListener(new PromptDialog.OnCountDownListener() {
                        @Override
                        public void onFinish() {
                            Toast.makeText(DialogActivity.this, "回掉完成", Toast.LENGTH_SHORT).show();
                        }
                    }).showSuccess("完成");
        } else if (view.getId() == R.id.tv_loading_1) {
            PromptDialog.newInstance(this)
                    .showLoading(PromptDialog.LOADING_POINT_ALPHA);
        } else if (view.getId() == R.id.tv_loading_2) {
            PromptDialog.newInstance(this)
                    .showLoading(PromptDialog.LOADING_POINT_TRANS);
        } else if (view.getId() == R.id.tv_loading_3) {
            PromptDialog.newInstance(this)
                    .showLoading(PromptDialog.LOADING_POINT_SCALE);
        } else if (view.getId() == R.id.tv_loading_4) {
            PromptDialog.newInstance(this)
                    .showLoading(PromptDialog.LOADING_RECT_SCALE);
        } else if (view.getId() == R.id.tv_loading_5) {
            PromptDialog.newInstance(this)
                    .showLoading(PromptDialog.LOADING_RECT_ALPHA);
        }
    }
}