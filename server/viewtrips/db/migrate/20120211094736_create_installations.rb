class CreateInstallations < ActiveRecord::Migration
  def change
    create_table :installations do |t|
      t.string :id
      t.date :on
      t.string :user

      t.timestamps
    end
  end
end
